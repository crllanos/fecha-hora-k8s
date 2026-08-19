# Date and time on K8S
Proof of concept on Kubernetes deploying a simple fullstack solution to get the current date and time.

## Environment

### VM setup


##### 2. Install KVM, QEMU, libvirt and interface

´´´bash

sudo apt update
sudo apt install -y qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils virtinst virt-manager

sudo usermod -aG libvirt $USER
sudo usermod -aG kvm $USER

´´´

