
String txt=new File("template.txt").text

def files=["rsremote.log","rsinfo.log","rsworker.log","rsconsole.log"]

int i=3
while (true){


i++;

i= i % 4;

def file=files.get(i);

int randomnumber=generateRandInt(0,txt.length()-50);


def curtxt=txt.substring(randomnumber ,randomnumber + 30)


def f1= new File(file)
f1 << "${new Date()}  ${curtxt}"


Thread.sleep(5000)

}


int generateRandInt(int Low,int High){


Random r = new Random();

int R = r.nextInt(High-Low) + Low;

print " Low $Low High $High R $R \n "
return R;

}