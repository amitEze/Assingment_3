//
// Created by shoham on 20/02/2022.
//
#include "../include/connectionHandler.h"

#ifndef ASSIGNMENT3_TASK_H
#define ASSIGNMENT3_TASK_H


class Task {
    private:
        bool &bye;
        ConnectionHandler &conn_;
    public:
        Task(bool &shutdown , ConnectionHandler &conn);
        void operator()();
};


#endif //ASSIGNMENT3_TASK_H
