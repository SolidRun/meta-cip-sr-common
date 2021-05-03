gst-launch-1.0 videotestsrc ! tee name=t ! queue ! autovideosink t. ! queue ! imxvpuenc_h264 bitrate=1000 ! udpsink host=192.168.1.10 port=9001
