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
  sh "jsvc #{options_str(options)} #{DAEMON_CLASS}"
end

CLEAN.include FileList['**/*.class']
CLEAN.include "erl4j.jar"
SRC = FileList['**/*.java']
OBJ = SRC.pathmap("%X.class")
ENV['CLASSPATH'] = find_jars
DAEMON_CLASS = 'com.syntacticbayleaves.erl4j.Erl4j'
PID_FILE = 'tmp/jsvc.pid'
LOG_FILE = 'tmp/erl4j.log'

rule ".class" => [".java"] do |t|
  sh "javac #{t.source}"
end

directory "tmp"
task :compile => [OBJ]
task :default => [:compile]

file "erl4j.jar" => [:compile] do |t|
  cd "src" do |path|
    sh "jar -cf #{t.name} #{OBJ.pathmap('%{src/,}p')}"
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

task :run => ["tmp", "erl4j.jar"] do |t|
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

