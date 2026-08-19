# Date and time on K8S


# Environment
# VM setup


# 2. Install KVM, QEMU, libvirt and interface

´´´bash
sudo apt update
sudo apt install -y qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils virtinst virt-manager

sudo usermod -aG libvirt $USER
sudo usermod -aG kvm $USER
newgrp libvirt

virsh uri # should return "qemu:///system"

sudo virt-install \
  --name ubuntu-dev-vm \
  --ram 3072 \
  --vcpus 2 \
  --cpu host-passthrough \
  --disk size=30,format=qcow2 \
  --os-variant ubuntu24.04 \
  --cdrom ~/isos/ubuntu-24.04.1-live-server-amd64.iso \
  --network network=default,model=virtio \
  --graphics vnc,listen=0.0.0.0 \
  --noautoconsole

´´´

