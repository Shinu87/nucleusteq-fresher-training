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
  let studenttotalmark = {};
  for (let obj of students) {
    let studentname = obj.name;
    let totalmark = 0;
    for (let mark of obj.marks) {
      totalmark += mark.score;
    }
    studenttotalmark[studentname] = totalmark;
  }
  return studenttotalmark;
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

// This function finds the topper in each subject for the given students.
// I used a nested loop because each student has multiple subjects.
// The outer loop goes through each student and the inner loop goes through each subject score.
// I keep a dictionary subjectToppers where each subject is a key.
// For each subject I store an array containing [highest score , student name].
// If a new student has a higher score than the current topper I update it.
// At the end I return the object containing all subject-wise toppers.
function subjectwisetopper(students) {
  let subjectToppers = {};

  for (let student of students) {
    let studentName = student.name;

    for (let mark of student.marks) {
      let subject = mark.subject;
      let score = mark.score;

      if (subject in subjectToppers) {
        if (subjectToppers[subject][0] < score) {
          subjectToppers[subject][0] = score;
          subjectToppers[subject][1] = studentName;
        }
      } else {
        subjectToppers[subject] = [score, studentName];
      }
    }
  }

  return subjectToppers;
}

// This function calculates the average score for each subject across all students.
// I first loop through each student and then through their marks array.
// For each subject I store all scores in an array inside totalsubjectscore object.
// After collecting all scores I loop through each subject array, calculate sum,
// and divide by number of scores to get the average.

function subjectwiseaverage(students) {
  let subjectScores = {};

  for (let student of students) {
    for (let subjectMark of student.marks) {
      let subject = subjectMark.subject;
      let score = subjectMark.score;

      if (subject in subjectScores) {
        subjectScores[subject].push(score);
      } else {
        subjectScores[subject] = [score];
      }
    }
  }

  for (let subject in subjectScores) {
    let totalCount = subjectScores[subject].length;
    let totalScore = 0;

    for (let score of subjectScores[subject]) {
      totalScore += score;
    }

    let avg = totalScore / totalCount;
    console.log("Average of " + subject + " is " + avg);
  }
}

// This function finds the overall class topper based on total marks.
// I first calculate total marks for each student using a helper function.
// Then I loop through each students total marks to find the maximum.
// Finally I print the name of the topper along with their total score.
function classtopper(students) {
  let studenttotalmarks = totalmarks(students);
  let studentName = null;
  let studentmaxtotalscore = 0;
  for (let key in studenttotalmarks) {
    if (studentmaxtotalscore < studenttotalmarks[key]) {
      studentName = key;
      studentmaxtotalscore = studenttotalmarks[key];
    }
  }
  console.log(
    "Class Topper is " + studentName + " with " + studentmaxtotalscore,
  );
}

console.log("---------------");
console.log("TOTAL MARKS");
let studenttotalmarks = totalmarks(students);
for (student in studenttotalmarks) {
  console.log(student + " total marks " + studenttotalmarks[student]);
}
console.log("---------------");

console.log("AVERAGE MARKS");
avgmarks(students);

console.log("---------------");

console.log("SUBJECT WISE TOPPER");
let toppers = subjectwisetopper(students);
for (let subject in toppers) {
  let [score, name] = toppers[subject];
  console.log(`${subject} topper is ${name} with score ${score}`);
}

console.log("---------------");
console.log("SUBJECT WISE AVERAGE");
subjectwiseaverage(students);

console.log("---------------");
console.log("CLASS TOPPER");
classtopper(students);
