# 3. Take user input (name and age) and print a formatted message.

name = input("Enter your name: ")
age = int(input("Enter your age: "))

print(f"Hello {name}! You are {age} years old.")


# 5. Write a program to swap two numbers.

x = int(input("\nEnter first number (x): "))
y = int(input("Enter second number (y): "))

print("\nBefore swapping: x =", x, ", y =", y)

# Method 1: using temporary variable
temp = x
x = y
y = temp

print("After swapping (method 1): x =", x, ", y =", y)


# Taking input again for second method
x = int(input("\nEnter x: "))
y = int(input("Enter y: "))

# Method 2: direct swap
x, y = y, x

print("After swapping (method 2): x =", x, ", y =", y)


# 6. Take two numbers and print sum, difference, multiplication, and division.

num1 = int(input("\nEnter first number: "))
num2 = int(input("Enter second number: "))

print("\nBasic Arithmetic Operations:")
print("Sum:", num1 + num2)
print("Difference:", num1 - num2)
print("Multiplication:", num1 * num2)

# Division with safety check
if num2 != 0:
    print("Division:", num1 / num2)
else:
    print("Division: Cannot divide by zero")
