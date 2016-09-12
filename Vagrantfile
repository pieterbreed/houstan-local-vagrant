# -*- mode: ruby -*-
# vi: set ft=ruby :

##################################################
#
# This block sets sane dev-time defaults for
# environment variables

$houstan = "HOUSTAN"


def ENV_ensure (name)
  if ENV[name].to_s.strip.length == 0
    puts "ENV VAR NOT SET: '#{name}'."
  end
end

[
  "#$houstan_IP",
  "#$houstan_HOSTNAME"
].each { |k| ENV_ensure k }

##################################################

Vagrant.configure("2") do |config|
  # https://docs.vagrantup.com.

  # config.vm.box = "ubuntu/trusty64"
  config.vm.box = "geerlingguy/ubuntu1604"
  config.vm.hostname = ENV["#$houstan_HOSTNAME"]

  # This IP goes with the IP as in the ansible inventory
  config.vm.network "private_network", ip: ENV['#$houstan_IP']

  config.vm.provider "virtualbox" do |vb|
    vb.gui = false
    vb.name = ENV['#$houstan_HOSTNAME']
    vb.memory = 1024
    vb.cpus = 2
    vb.customize ["modifyvm", :id, "--cpuexecutioncap", "50"]
  end

end
