# -*- mode: ruby -*-
# vi: set ft=ruby :

########################################

$houstan = "HOUSTAN"

########################################

def ENV_ensure (name)
  if ENV[name].to_s.strip.length == 0
    abort "Set ENV variable: '#{name}'."
  end
end

[
  "#{$houstan}_IP",
  "#{$houstan}_HOSTNAME"
].each { |k| ENV_ensure k }

########################################

Vagrant.configure("2") do |config|
  config.vm.box = "geerlingguy/ubuntu1604"
  config.vm.hostname = ENV["#{$houstan}_HOSTNAME"]
  config.vm.network "private_network", ip: ENV["#{$houstan}_IP"]

  config.vm.provider "virtualbox" do |vb|
    vb.gui = false
    vb.name = ENV["#{$houstan}_HOSTNAME"]
    vb.memory = 1024
    vb.cpus = 2
    vb.customize ["modifyvm", :id, "--cpuexecutioncap", "50"]
  end

  config.vm.provision "file", source: "~/.ssh/id_rsa.pub", destination: "~/.ssh/me.pub"
  config.vm.provision "shell", inline: "sudo -u vagrant bash -c \"cat /home/vagrant/.ssh/me.pub >> /home/vagrant/.ssh/authorized_keys\"" 

end
