package com.cocos2dj.platform;

public enum ResolutionPolicy
{
    /** The entire application is visible in the specified area without trying to preserve the original aspect ratio.
    // Distortion can occur, and the application may appear stretched or compressed. */
    EXACT_FIT,
    /** The entire application fills the specified area, without distortion but possibly with some cropping,
    // while maintaining the original aspect ratio of the application. */
    NO_BORDER,
    /** The entire application is visible in the specified area without distortion while maintaining the original
    // aspect ratio of the application. Borders can appear on two sides of the application. */
    SHOW_ALL,
    /** The application takes the height of the design resolution size and modifies the width of the internal
    // canvas so that it fits the aspect ratio of the device
    // no distortion will occur however you must make sure your application works on different */
    // aspect ratios
    FIXED_HEIGHT,
    /** The application takes the width of the design resolution size and modifies the height of the internal
    // canvas so that it fits the aspect ratio of the device
    // no distortion will occur however you must make sure your application works on different
    // aspect ratios */
    FIXED_WIDTH,

    UNKNOWN,
};
