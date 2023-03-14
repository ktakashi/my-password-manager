#!/bin/bash

docker pull ktakashi/sagittarius:edge

docker run -it \
       --mount src=$(pwd),target=/scheme,type=bind \
       --mount src=/tmp,target=/tmp/cache,type=bind \
       --env SAGITTARIUS_CACHE_DIR=/tmp/cache \
       ktakashi/sagittarius:edge -L/scheme/lib /scheme/main.scm
