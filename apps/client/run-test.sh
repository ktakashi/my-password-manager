#!/bin/bash

docker pull ktakashi/sagittarius:edge

RESULT=0
for file in $(find tests -name '*.scm'); do
    echo Testing ${file}

    docker run -it \
    	   --mount src=$(pwd),target=/scheme,type=bind \
    	   --mount src=/tmp,target=/tmp/cache,type=bind \
    	   --env SAGITTARIUS_CACHE_DIR=/tmp/cache \
    	   ktakashi/sagittarius:edge -L/scheme/lib /scheme/${file}
    if [ $? -ne 0 ]; then
	RESULT=1
    fi
done

if [ ${RESULT} -ne 0 ]; then
    echo Test execution failed
    exit 1
fi
