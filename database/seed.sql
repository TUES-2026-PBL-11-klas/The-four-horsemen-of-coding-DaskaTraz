INSERT INTO users (username, email, password_hash, role)
VALUES
('student1', 's1@mail.com', 'hash', 'student'),
('teacher1', 't1@mail.com', 'hash', 'teacher');

INSERT INTO classes (name, graduation_year)
VALUES ('11A', 2026);

INSERT INTO students (user_id, class_id, number_in_class)
VALUES (1, 1, 1);

INSERT INTO teachers (user_id)
VALUES (2);

INSERT INTO subjects (name)
VALUES ('Math');

INSERT INTO teachers_subjects (teacher_id, subject_id, class_id)
VALUES (1, 1, 1);

INSERT INTO grades (value, student_id, subject_id, teacher_id)
VALUES (5.50, 1, 1, 1);