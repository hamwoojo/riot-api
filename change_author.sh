#!/bin/bash

if [ "$GIT_AUTHOR_NAME" = "woojo" ]; then
    GIT_AUTHOR_NAME="hamwoojo"
    GIT_AUTHOR_EMAIL="woojo3398@naver.com"
    GIT_COMMITTER_NAME="hamwoojo"
    GIT_COMMITTER_EMAIL="woojo3398@naver.com"
    git commit-tree "$@"
else
    git commit-tree "$@"
fi
