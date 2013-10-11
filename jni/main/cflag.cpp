/*
 * cflag.cpp
 * Trigger file to notify application a command finished
 *  Created on: Oct 10, 2013
 *      Author: holmes
 */

#include <fcntl.h>
#include <unistd.h>

int main(int argc, char **args){
	if (argc > 1){
		char* file_path = args[1];
		int fd = open(file_path, O_RDONLY);
		if (fd > 0){
			close(fd);
			return 0;
		}
		return 2;
	}
	return 1;
}

