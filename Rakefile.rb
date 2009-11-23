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
CLEAN.include "build"

JARS = ['erl4j', 'erl4j-sample']

SRC = JARS.inject({}) do |result, element|
  result[element] = FileList[element + '/**/*.java']
  result
end

OBJ = JARS.inject({}) do |result, element|
  result[element] = SRC[element].pathmap("build/%X.class")
  result
end

ENV['CLASSPATH'] = find_jars + ":" + JARS.join(":")

directory "tmp"

JARS.each do |jar_name|
  directory "build/#{jar_name}"
end

task :default => [:compile, :jar]

desc "Compiles all source files"
task :compile => [:compile_lib, :compile_sample]

desc "Compiles erl4j source files"
task :compile_lib => 
  ["build/erl4j"] + OBJ["erl4j"]

desc "Compiles erl4j-sample source files"
task :compile_sample => 
  [:compile_lib, "build/erl4j-sample"] + OBJ["erl4j-sample"]

desc "Creates erl4j.jar and erl4j-sample.jar"
task :jar => JARS.map {|jar_name| "build/#{jar_name}.jar"}


desc "Start the erl4j as a daemon"
task :start => [:compile, :jar] do |t|
  sh "bin/erl4jctl start"
end

desc "Run the erl4j in debug mode"
task :run => [:compile, :jar] do |t|
  sh "bin/erl4jctl run"
end

desc "Stop the erl4j daemon"
task :stop do |t|
  sh "bin/erl4jctl stop"
end

rule ".class" => 
  [proc { |task_name| "#{task_name.pathmap('%{build/,}X.java')}" }] do |t|
    sh "javac -d #{t.name.split('/')[0..1].join('/')} #{t.source}"
end

rule ".jar" => 
  [proc {|task_name| "#{task_name.pathmap('%X')}"}] do |t|
    puts t.name
    sh "jar -cf #{t.name} -C #{t.name.pathmap('%X')} ."
end


