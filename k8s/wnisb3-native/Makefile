# Copyright 2022 VMware. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

all: install

install:
	kustomize build . | kbld -f- | kapp deploy -a wnisb3-native -c -y -f-

uninstall:
	kapp delete -a wnisb3-native -y
