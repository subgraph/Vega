#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/stat.h>

static int do_mount(char *);
static int do_umount();
static void exec_mount(char *);
static void exec_umount();
static int wait_status(pid_t);
int
main(int argc, char** argv)
{
    if(argc != 2) {
    	fprintf(stderr, "dmg-mount <file>\n");
        exit(EXIT_FAILURE);
    }
    setuid(0);
    if(strcmp(argv[1], "-u") == 0)  {
        chmod("/mnt/dmg", 0755);
        do_umount();
    } else {
        if(do_mount(argv[1]) == 0)
			chmod("/mnt/dmg", 0777);
    }
    exit(EXIT_SUCCESS);
}

static int
do_mount(char *path) 
{
    pid_t pid;
    if((pid = fork()) == -1) {
        fprintf(stderr, "fork() failed\n");
        exit(EXIT_FAILURE);
    }
    if(pid == 0)  {
        exec_mount(path);
		return -1;
	}
    else {
        return wait_status(pid);
	}
    
}

static int
do_umount()
{
    pid_t pid;
    if((pid = fork()) == -1) {
        fprintf(stderr, "fork() failed\n");
        exit(EXIT_FAILURE);
    }
    if(pid == 0) {
        exec_umount();
		return -1;
	}
    else {
        return wait_status(pid);
	}
}

static void
exec_mount(char *path)
{
    char *args[6];
    char *env[] = {NULL};

    args[0] = "/bin/mount";
    args[1] = "-o";
    args[2] = "loop,nosuid";
    args[3] = path;
    args[4] = "/mnt/dmg";
    args[5] = NULL;
    execve("/bin/mount", args, env);
}

static void 
exec_umount() 
{
    char *args[3];
    char *env[] = {NULL};
    args[0] = "/bin/umount";
    args[1] = "/mnt/dmg";
    args[2] = NULL;
    execve("/bin/umount", args, env);
}

static int
wait_status(pid_t pid)
{
    int status;

    if(waitpid(pid, &status, 0) == -1) {
        fprintf(stderr, "waitpid() failed.");
        exit(EXIT_FAILURE);
    }

    if(WIFEXITED(status)) 
      return(status);
    
    return -1;
}
