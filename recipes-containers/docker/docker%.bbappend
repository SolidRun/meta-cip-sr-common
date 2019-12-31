# Change /var/lib/docker to a soft link to /data/.var/lib/docker
#    and add a soft link for /data/docker to /data/.var/lib/docker
# Change /opt/containerd to a soft link to /data/.opt/containerd

do_install_append () {
    install -d ${D}${localstatedir}/lib
    install -d ${D}/data/.var/lib/docker
    ln -s /data/.var/lib/docker ${D}${localstatedir}/lib/docker
    ln -s /data/.var/lib/docker ${D}/data/docker

    install -d ${D}/opt
    install -d ${D}/data/.opt/containerd/bin
    install -d ${D}/data/.opt/containerd/lib
    ln -s /data/.opt/containerd ${D}/opt/containerd

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/docker.init ${D}${sysconfdir}/init.d/docker.init
}

FILES_${PN} += " \
    /data/.opt/containerd/bin \
    /data/.opt/containerd/lib \
    /data/.var/lib/docker \
    /data/docker \
    /opt/containerd \
"
