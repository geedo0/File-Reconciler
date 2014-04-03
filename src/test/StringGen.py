#!/usr/bin/python

#import string
#import random
import uuid



size=100 #this is the desired size in megabytes

N=size*1000000
strs=list()

def my_random_string(string_length=10):
    """Returns a random string of length string_length."""
    i =1
    while (i*32<string_length):
		random = str(uuid.uuid4())
		random = random.replace("-","")
		strs.append(random)
		i+=1

    text= ''.join(strs)	
    return text[0:string_length]

    #random = str(uuid.uuid4()) # Convert uuid format to python string.
    #random = random.upper() # Make all characters uppercase.
    #random = random.replace("-","") # Remove the uuid '-'.
    #print (len(random))

    #return random[0:string_length] # Return the random string.

print(my_random_string(N)) # eg: D9E50C



"""
str=''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(N))

print(str)"""