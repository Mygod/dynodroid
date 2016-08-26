import sys
import os
import imp
import stat
import shutil
import subprocess

#Input
svnDir = ""
destDir = ""

#relative locations
setupRelPath = "/dynodroidsetup"
toolsRelPath = "/tools"
libRelPath = "/libs"
srcRelPath = "/src"
buildXmlPath = "/build.xml"
propertiesFileName = "dynodroid.properties"

#Input Validation
def checkParams(arguments):
    if (len(arguments) != 3):
        print "Wrong Usage Buddy!!!"
        print "Usage:",arguments[0]," <path_To_The_Svn_Path_of_Dynodroid> <path_To_The_Deployment_Directory>"
        sys.exit(-1)

    if (not os.path.isdir(arguments[1])):
        print "Looks like the provided directory:",arguments[1]," doesn't exist"
        print "Please check this and retry"
        sys.exit(-2)

    if(os.path.isdir(arguments[2])):
        print "Warning: The provided target directory:",arguments[2]," exist,contents will be replaced"
    else:
        os.makedirs(arguments[2])

    #Copy the provided data to the global variables
    global svnDir
    global destDir
    svnDir = os.path.abspath(arguments[1])
    destDir = os.path.abspath(arguments[2])
    

#copy src folder to the destination dir
def copyDirectory(srcDir,destDir):    
    if(not os.path.isdir(srcDir)):
        print "Error: Src folder doesn't exist:",srcDir
        return -1

    if (os.path.exists(destDir)):
         shutil.rmtree(destDir,True)
         
    shutil.copytree(srcDir,destDir)
    return 0


#copy file from src to destination
def copyFile(srcFile,destFile):    
    if(not os.path.isfile(srcFile)):
        print "Error: Src File is not present at the location:",srcFile
        return -1

    if (not os.path.exists(os.path.dirname(destFile))):
         os.makedirs(os.path.dirname(destFile))
         
    shutil.copyfile(srcFile,destFile)
    return 0


#Main Code
print " ****  *     * |\    |   ****         ****    ****    ****   *****   ****  \n"
print "|    *  \   /  | \   |  *    *   *** |    *  |   /   *     *   |    |    * \n"
print "|    *   \ /   |  \  | *      *  *** |    *  |  /   *       *  |    |    * \n"
print "|    *    |    |   \ | *      *      |    *  | /    *       *  |    |    * \n"
print "|    *    |    |    \|  *    *       |    *  | \     *     *   |    |    * \n"
print " ****     *    |     |   ****         ****   |  \      ****  *****   ****  \n"
checkParams(sys.argv)   
sdkInstallPath = os.environ['ANDROID_HOME']

if sdkInstallPath is None:
    print "You need to set ANDROID_HOME environment variable"
    sys.exit(-4)
    
#copy m3setup
if(copyDirectory(svnDir+setupRelPath,destDir+setupRelPath) != 0):
    print "Problem occured while copying m3setup folder"
    sys.exit(-3)
    
#copy src
if(copyDirectory(svnDir+srcRelPath,destDir+srcRelPath) != 0):
    print "Problem occured while copying src folder"
    sys.exit(-3)

#copy libs
if(copyDirectory(svnDir+libRelPath,destDir+libRelPath) != 0):
    print "Problem occured while copying lib folder"
    sys.exit(-3)

#copy tools
if(copyDirectory(svnDir+toolsRelPath,destDir+toolsRelPath) != 0):
    print "Problem occured while copying tools folder"
    sys.exit(-3)

#copy build.xml
if(copyFile(svnDir+buildXmlPath,destDir+buildXmlPath) != 0):
    print "Problem occured while copying build.xml"
    sys.exit(-3)


avdPath = os.path.expanduser("~/.android/avd")
#generate m3.properties
f = open(destDir+"/"+propertiesFileName, 'w')
f.write("work_dir="+destDir+"/workingDir\n")
f.write("sdk_install="+sdkInstallPath+"\n")
f.write("app_dir="+destDir+"/apps\n")
f.write("test_strategy=WidgetBasedTesting\n")
f.write("sel_stra=RandomBiasBased\n")
f.write("max_widgets=1000\n")
f.write("avd_store="+avdPath+"\n")
f.write("event_count=100,1000\n")
f.write("apktool_loc="+destDir+"/tools/apktool/apktool.jar\n")
f.write("tools_dir="+destDir+"/tools/\n")
f.write("monkeyrunner_script="+destDir+setupRelPath+"/monkeyrunner/monkeyrunner.py\n")
f.write("complete_notify=someone@example.com\n")
f.write("report_email_user=reportSourceUserName@gmail.com\n")
f.write("report_email_pass=password\n")

#Below are the settings used by the underlying RMI engine
#DBConnection String in form of : server;dbname;username;password
f.write("rmi_db=localhost;m3db;root;password\n")

#fully qualified Server name
f.write("apk_srv=pag-www.gtisc.gatech.edu\n")
f.write("res_srv=pag-www.gtisc.gatech.edu\n")
f.write("res_pub_srv=pag-www.gtisc.gatech.edu\n")

#These are paths
f.write("res_dwn=/pth/to/folder/in/res_srv/where/results/need/to/be/stored/for/public/download\n")
f.write("res_rem_path=/pth/to/folder/in/res_srv/where/complete/results/need/to/be/stored\n")
f.write("apk_rem_path=/pth/to/app File or folder/in/apk_srv/where/apps/need/to/be/copied/from\n")

#This the the user name used to do scp
f.write("scp_user_name=usename_need_to_do_scp")
try:
    os.makedirs(destDir+"/apps")
except OSError:
    pass
print "Sucess: Deploying Dynodroid to the target folder\n"
print "\t"+propertiesFileName+" have been created\n"
print "\nTHINGS TO DO BEFORE YOU RUN Dynodroid\n"
print "\t1)Copy the apps that needs to be tested to the folder:",destDir+"/apps\n"
print "\t2)[Optional] Modify the required TestStratgey,SelectionStrategy and number of events\n"
print "\t3)[Optional] Add the text required in to textBoxInput files under the src folder of the app\n"
print "\t4)Start up emulators and/or connect devices\n"
print "\nNote:All Logs will be created in folder:",destDir+"/workingDir\n"
print "\n\nAfter you do all above : Browse to the folder:",destDir," and run : ant clean,ant compile,ant run\n"
print "\n\nWhile its running you probably want to do something else as it takes a bit of time\n"
print "\n\n\tEnjoy Using Dynodroid\n"
