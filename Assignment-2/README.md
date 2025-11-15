📘 Assignment 2 – Overview (Text Points)

📄 This assignment focuses on building a basic text encoder and decoder using a shift-based cipher in Java.

🔢 The user enters a shift value, and the program applies it to encode or decode messages.

🔠 The program correctly handles uppercase and lowercase letters separately.

🧮 It uses modulo 26 to ensure alphabetical wrap-around for shifting.

✨ Characters like numbers, spaces, and punctuation remain unchanged.

🔄 The menu allows users to choose Encode, Decode, or Exit, and supports multiple operations in a loop.

🧩 Decoding works by applying the negative shift to the text.

⚙️ Installation & Running Steps (Text Points)

📥 Download or clone the repository:
git clone https://github.com/Chiranjivi-R/VaultOfCode_internship

📂 Open the Assignment-2 folder inside the repo.

🛠 Compile the Java program using:
javac TextCipher.java

▶️ Run the program using:
java TextCipher

🧠 Working Mechanism (Text Points)

🔄 Each alphabet character is shifted forward or backward based on the shift value.

🔠 Uppercase (A–Z) and lowercase (a–z) are processed independently for accuracy.

➿ Alphabet wrap-around is handled using shift % 26.

✨ Non-alphabetic characters (spaces, digits, symbols) are left unchanged.

🔁 The program keeps running until the user selects the Exit option from the menu.
