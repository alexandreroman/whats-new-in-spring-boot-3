# Copyright 2022 VMware. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

all: create-cluster

create-cluster:
	kindops create && kindops install monitoring && kindops install knative-serving

destroy-cluster:
	kindops destroy
