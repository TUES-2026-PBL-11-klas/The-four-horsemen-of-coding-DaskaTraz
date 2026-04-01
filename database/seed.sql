INSERT INTO users (username, email, password_hash, role)
VALUES
('student1', 'student1@mail.com', 'hash', 'student'),
('student2', 'student2@mail.com', 'hash', 'student'),
('teacher1', 'teacher@mail.com', 'hash', 'teacher');

INSERT INTO classes (name, graduation_year)
VALUES ('11A', 2026);

INSERT INTO students (user_id, class_id, number_in_class)
VALUES
(1, 1, 1),
(2, 1, 2);

INSERT INTO teachers (user_id)
VALUES (3);

INSERT INTO subjects (name)
VALUES ('Mathematics'), ('Physics');

INSERT INTO teachers_subjects (teacher_id, subject_id)
VALUES (1, 1), (1, 2);

INSERT INTO grades (value, student_id, subject_id, teacher_id)
VALUES
(5.50, 1, 1, 1),
(4.75, 1, 2, 1),
(6.00, 2, 1, 1);

INSERT INTO timetable (class_id, subject_id, teacher_id, day_of_week, lesson_number, start_at, duration_minutes)
VALUES
(1, 1, 1, 1, 1, '08:00', 45),
(1, 2, 1, 3, 2, '09:00', 45);

INSERT INTO lesson_details (timetable_id, lesson_type, description)
VALUES
(1, 'lecture', 'Algebra basics'),
(2, 'lab', 'Physics experiments');