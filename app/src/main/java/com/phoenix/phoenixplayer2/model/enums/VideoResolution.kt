package com.phoenix.phoenixplayer2.model.enums

enum class VideoResolution(
    val width: Int, val height: Int
) {
    NONE(-1, -1),
    SD(0, 0),
    HD(1280, 720),
    FHD(1920, 1080),
    UHD(3840, 2160),
    FOUR_K(4096, 2160);

}