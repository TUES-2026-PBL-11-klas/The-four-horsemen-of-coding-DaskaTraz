ALTER TABLE public.users
    DROP CONSTRAINT IF EXISTS users_role_check,
    ADD CONSTRAINT users_role_check
        CHECK (role IN ('student', 'teacher', 'admin', 'NOT_PROVIDED'));