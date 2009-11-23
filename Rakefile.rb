#
# The following executables must be in your PATH:
#  * find
#  * paste
#  * javac
#  * jar
#  * jsvc 1.0.1 (http://commons.apache.org/daemon/jsvc.html)
#
# Note that if you're running Snow Leopard, you must build jsvc 
# with this patch:  http://issues.apache.org/jira/browse/DAEMON-129
#


require 'rake/clean'

def find_jars
  `find . -name "*.jar" | paste -s -d':' -`.strip
end

CLEAN.include FileList['**/*.class']
CLEAN.include "src/erl4j.jar"
CLEAN.include "sample/erl4j-sample.jar"

LIB_SRC = FileList['src/**/*.java']
LIB_OBJ = LIB_SRC.pathmap("%X.class")

SAMPLE_SRC = FileList['sample/**/*.java']
SAMPLE_OBJ = SAMPLE_SRC.pathmap("%X.class")

ENV['CLASSPATH'] = find_jars + ":src:sample"

directory "tmp"

task :default => [:compile_lib]

rule ".class" => [".java"] do |t|
  sh "javac #{t.source}"
end

task :compile_lib => LIB_OBJ
task :compile_sample => LIB_OBJ + SAMPLE_OBJ

file "erl4j.jar" => [:compile_lib] do |t|
  cd "src" do |path|
    sh "jar -cf #{t.name} #{LIB_OBJ.pathmap('%{src/,}p')}"
  end
end

file "erl4j-sample.jar" => [:compile_sample] do |t|
  cd "sample" do |path|
    sh "jar -cf #{t.name} #{SAMPLE_OBJ.pathmap('%{sample/,}p')}"
  end
end

task :start => ["tmp", "erl4j.jar"] do |t|
  sh "bin/erl4jctl start"
end

task :run => ["tmp", "erl4j.jar", "erl4j-sample.jar"] do |t|
  sh "bin/erl4jctl run"
end

task :stop do |t|
  sh "bin/erl4jctl stop"
end

