#!/bin/bash

CONFIG=local.yaml
if [ x"$1" != x"" ]; then
    CONFIG=$1
fi

docker pull ktakashi/sagittarius:edge

docker run -it \
       --mount src=$(pwd),target=/scheme,type=bind \
       --mount src=/tmp,target=/tmp/cache,type=bind \
       --env SAGITTARIUS_CACHE_DIR=/tmp/cache \
       ktakashi/sagittarius:edge -L/scheme/lib /scheme/main.scm -c /scheme/$CONFIG
