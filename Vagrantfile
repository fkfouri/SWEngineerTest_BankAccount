Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/trusty64"

  config.vm.provision "shell", inline: <<-SHELL
    sudo add-apt-repository ppa:openjdk-r/ppa
    sudo apt-get update
    sudo apt-get install openjdk-8-jdk
    sudo wget -P ~/bin https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
    sudo chmod a+x ~/bin/lein
    sudo lein
    sudo apt-get install git-all
  SHELL
end