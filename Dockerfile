FROM ubuntu:latest
LABEL authors="thefr"

ENTRYPOINT ["top", "-b"]