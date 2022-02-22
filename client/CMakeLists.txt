cmake_minimum_required(VERSION 3.21)
project(assignment3)

set(CMAKE_CXX_STANDARD 14)

add_executable(assignment3
        src/echoClient.cpp
        src/connectionHandler.cpp src/Task.cpp include/Task.h)

