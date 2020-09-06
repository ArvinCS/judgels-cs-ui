#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-backends/jerahmeel/jerahmeel-dist

../../gradlew clean distTar
tar -xf build/distributions/jerahmeel-* --strip-components=1 -C build/distributions
rm build/distributions/*.tgz
cp init.sh build/distributions

cd ../../../deployment/ansible

ansible --version
ansible-playbook playbooks/build-jerahmeel.yml
