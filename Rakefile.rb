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

def options_str(options)
  options.inject("") do |result, keyval|
    key, val = keyval
    if val 
      result + "-#{key} '#{val}' "
    else 
      result + "-#{key} "
    end
  end.strip
end

def jsvc (options)
  sh "jsvc #{options_str(options)} #{DAEMON_CLASS} #{NODE_NAME} #{DISPATCHER_CLASS} #{TIMEOUT}"
end

CLEAN.include FileList['**/*.class']
CLEAN.include "src/erl4j.jar"
CLEAN.include "sample/erl4j-sample.jar"

LIB_SRC = FileList['src/**/*.java']
LIB_OBJ = LIB_SRC.pathmap("%X.class")

SAMPLE_SRC = FileList['sample/**/*.java']
SAMPLE_OBJ = SAMPLE_SRC.pathmap("%X.class")

ENV['CLASSPATH'] = find_jars + ":src:sample"
DAEMON_CLASS = 'com.syntacticbayleaves.erl4j.Erl4j'
PID_FILE = 'tmp/jsvc.pid'
LOG_FILE = 'tmp/erl4j.log'

NODE_NAME = 'erl4j@' + `hostname`.strip
DISPATCHER_CLASS = 'com.syntacticbayleaves.erl4j.SampleErl4jDispatcher'
TIMEOUT = 2000

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
  options = {
    :outfile => LOG_FILE,
    :errfile => '&1',
    :pidfile => PID_FILE,
    :cp      => find_jars,
    :debug   => nil
  }
  
  jsvc options
end

task :run => ["tmp", "erl4j.jar", "erl4j-sample.jar"] do |t|
  options = {
    :pidfile  => PID_FILE,
    :cp       => find_jars,
    :debug    => nil,
    :nodetach => nil
  }

  jsvc options
end

task :stop do |t|
  options = {
    :pidfile => PID_FILE,
    :cp      => find_jars,
    :stop    => nil
  }

  jsvc options
end

