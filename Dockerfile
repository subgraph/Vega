# first stage, build Vega

FROM ubuntu:xenial

WORKDIR /vega

ADD . /vega

RUN apt-get update && apt-get install -y \
    libwebkitgtk-1.0 \
    openjdk-8-jre-headless \
    ant \
    python3 \
    python3-pip \
    git \
    xvfb \
    && rm -rf /var/lib/apt/lists/*

RUN pip3 install py4j

RUN ant

# second stage, extract Vega

FROM ubuntu:xenial

WORKDIR /vega

RUN apt-get update && apt-get install -y \
    unzip \
    openjdk-8-jre-headless \
    libwebkitgtk-1.0 \
    xvfb \
    && rm -rf /var/lib/apt/lists/*

COPY --from=0 /vega/build/stage/I.VegaBuild/VegaBuild-linux.gtk.x86_64.zip .

RUN unzip VegaBuild-linux.gtk.x86_64.zip -d .


