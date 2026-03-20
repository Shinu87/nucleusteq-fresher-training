console.log("Student Analyzer Started");
const students = [
  {
    name: "Lalit",
    marks: [
      { subject: "Math", score: 78 },
      { subject: "English", score: 82 },
      { subject: "Science", score: 74 },
      { subject: "History", score: 69 },
      { subject: "Computer", score: 88 },
    ],
    attendance: 82,
  },
  {
    name: "Rahul",
    marks: [
      { subject: "Math", score: 90 },
      { subject: "English", score: 85 },
      { subject: "Science", score: 80 },
      { subject: "History", score: 76 },
      { subject: "Computer", score: 92 },
    ],
    attendance: 91,
  },
  {
    name: "Aman",
    marks: [
      { subject: "Math", score: 45 },
      { subject: "English", score: 50 },
      { subject: "Science", score: 38 }, // fail case
      { subject: "History", score: 55 },
      { subject: "Computer", score: 60 },
    ],
    attendance: 78,
  },
  {
    name: "Riya",
    marks: [
      { subject: "Math", score: 88 },
      { subject: "English", score: 92 },
      { subject: "Science", score: 85 },
      { subject: "History", score: 90 },
      { subject: "Computer", score: 95 },
    ],
    attendance: 72, // low attendance case
  },
  {
    name: "Sneha",
    marks: [
      { subject: "Math", score: 65 },
      { subject: "English", score: 70 },
      { subject: "Science", score: 68 },
      { subject: "History", score: 72 },
      { subject: "Computer", score: 75 },
    ],
    attendance: 88,
  },
];

// I used a nested loop approach because each student has multiple subjects,
// so I first loop through all students and then loop through their marks.
// I chose for...of because I need direct access to objects
// For each student, I reset total marks to 0 and keep adding each subject score.
// This helps in calculating the total marks accurately for every student.
function totalmarks(students) {
  for (let obj of students) {
    let studentname = obj.name;
    let totalmark = 0;
    for (let mark of obj.marks) {
      totalmark += mark.score;
    }
    console.log(studentname + " total marks " + totalmark);
  }
}
// I created this function to calculate average marks for each student.
// I used a loop to go through each student because the data is stored in an array.
// Inside that, I used another loop to go through each subject's marks,
// since marks are also stored as an array inside each student.
// I first calculate total marks and then divide by number of subjects
// to get the average for each student.

function avgmarks(students) {
  for (let obj of students) {
    let totalsubjects = obj.marks.length;
    let studentname = obj.name;
    let totalmark = 0;
    for (let mark of obj.marks) {
      totalmark += mark.score;
    }
    let average = totalmark / totalsubjects;
    console.log(studentname + " average marks is " + average);
  }
}
console.log("---------------");
console.log("TOTAL MARKS");
totalmarks(students);
console.log("---------------");
console.log("AVERAGE MARKS");
avgmarks(students);
