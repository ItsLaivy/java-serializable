cmake_minimum_required(VERSION 2.8...3.28)
set(CMAKE_C_STANDARD 11)

# Project information
project(
        java-serializable
        VERSION 1.1.0
        DESCRIPTION "The C implementation for java-serializable to allocate objects without passing by the constructor"
        HOMEPAGE_URL "https://github.com/ItsLaivy/java-serializable/"
        LANGUAGES C
)

# Variables
set(LIBRARY_DIR src/main/resources/libs)
file(MAKE_DIRECTORY ${LIBRARY_DIR})

# Include the allocator file
add_library(Allocator SHARED
        src/main/c/allocator.c
        src/main/c/codes_laivy_serializable_Allocator.h
)

# JNI Directories
set(JAVA_HOME "C:/Program Files/Java/jdk-1.8")
include_directories(${JAVA_HOME}/include)
include_directories(${JAVA_HOME}/include/win32)

find_package(JNI REQUIRED)

# Define custom commands to build DLLs
add_custom_command(
        TARGET Allocator
        POST_BUILD
        COMMAND "C:/mingw/bin/x86_64-w64-mingw32-gcc.exe" -m64 -shared -o ${LIBRARY_DIR}/win64.dll src/main/c/*.c -I "${JAVA_HOME}/include" -I "${JAVA_HOME}/include/win32"
        WORKING_DIRECTORY ${CMAKE_SOURCE_DIR}
        COMMENT "Compiling libraries for Windows 64-bit..."
)