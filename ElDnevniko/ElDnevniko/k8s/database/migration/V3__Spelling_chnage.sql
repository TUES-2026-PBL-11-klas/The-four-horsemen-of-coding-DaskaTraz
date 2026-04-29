-- Drop the existing constraint
ALTER TABLE public.users DROP CONSTRAINT users_role_check;

-- Update existing data
UPDATE public.users SET role = CASE 
    WHEN role = 'NOT_PROVIDED' THEN 'NOTPROVIDED'
    WHEN role = 'student' THEN 'STUDENT'
    WHEN role = 'teacher' THEN 'TEACHER'
    WHEN role = 'admin' THEN 'ADMIN'
    ELSE role
END;

-- Add the updated constraint
ALTER TABLE public.users ADD CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['STUDENT'::character varying, 'TEACHER'::character varying, 'ADMIN'::character varying, 'NOTPROVIDED'::character varying])::text[])));