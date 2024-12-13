int x = 0;
int y = 0;
int z = 0;
for(int i = 0; i < 400; i++)
{
    x++;
    if(x > 30)
    {
        y--;
    }
    if(y < -40)
    {
        z +=5;
    }
}
