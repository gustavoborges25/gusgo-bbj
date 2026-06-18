-- Criação da tabela de academias
CREATE TABLE academies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
-- Criação do tipo ENUM para as regras de acesso
CREATE TYPE user_role AS ENUM ('OWNER', 'PROFESSOR', 'ALUNO');

-- Criação da tabela de usuários
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    academy_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Cria o vínculo oficial com a tabela de academias (Chave Estrangeira)
    CONSTRAINT fk_academy
        FOREIGN KEY (academy_id)
        REFERENCES academies (id)
        ON DELETE CASCADE
);
-- Criação da tabela de faixas
CREATE TABLE belts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    color VARCHAR(7) NOT NULL,
    order_position INTEGER NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
-- insert default de faixas
INSERT INTO belts (name, color, order_position) VALUES
('Branca', '#FFFFFF', 1),
('Cinza', '#808080', 2),
('Amarela', '#FFD700', 3),
('Laranja', '#FF8C00', 4),
('Verde', '#008000', 5),
('Azul', '#002D62', 6),
('Roxa', '#4B0082', 7),
('Marrom', '#8B4513', 8),
('Preta', '#000000', 9);
-- Criação da tabela de professores
CREATE TABLE instructors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    academy_id UUID NOT NULL,
    user_id UUID NOT NULL UNIQUE,
    belt_id UUID NOT NULL,
    degree INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Academias
    CONSTRAINT fk_instructor_academy
        FOREIGN KEY (academy_id)
        REFERENCES academies (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para Usuários
    CONSTRAINT fk_instructor_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para Faixas
    CONSTRAINT fk_instructor_belt
        FOREIGN KEY (belt_id)
        REFERENCES belts (id)
        ON DELETE RESTRICT
);
-- Criação da tabela de alunos
CREATE TABLE students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    academy_id UUID NOT NULL,
    user_id UUID UNIQUE,
    name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    belt_id UUID NOT NULL,
    degree INTEGER NOT NULL DEFAULT 0,
    join_date DATE NOT NULL DEFAULT CURRENT_DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Academias
    CONSTRAINT fk_student_academy
        FOREIGN KEY (academy_id)
        REFERENCES academies (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para Usuários (Opcional/Omissível)
    CONSTRAINT fk_student_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE SET NULL,
    -- Chave Estrangeira para Faixas
    CONSTRAINT fk_student_belt
        FOREIGN KEY (belt_id)
        REFERENCES belts (id)
        ON DELETE RESTRICT
);
-- Criação da tabela de metodologias - Ex: Infantil, adulto, no-gi, feminino
CREATE TABLE curriculums (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    academy_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Academias
    CONSTRAINT fk_curriculum_academy
        FOREIGN KEY (academy_id)
        REFERENCES academies (id)
        ON DELETE CASCADE
);
-- Criação da tabela que liga metodologia a faixa
CREATE TABLE curriculum_belts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    curriculum_id UUID NOT NULL,
    belt_id UUID NOT NULL,
    order_position INTEGER NOT NULL, -- Define a ordem da faixa dentro DESTA metodologia
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Currículos
    CONSTRAINT fk_curriculum_belt_curriculum
        FOREIGN KEY (curriculum_id)
        REFERENCES curriculums (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para Faixas
    CONSTRAINT fk_curriculum_belt_belt
        FOREIGN KEY (belt_id)
        REFERENCES belts (id)
        ON DELETE RESTRICT,
    -- Impede duplicar a mesma faixa dentro do mesmo currículo
    CONSTRAINT uq_curriculum_belt
        UNIQUE (curriculum_id, belt_id)
);
-- Criação da tabela de módulos
CREATE TABLE modules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    curriculum_belt_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    order_position INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para a tabela intermediária curriculum_belts
    CONSTRAINT fk_module_curriculum_belt
        FOREIGN KEY (curriculum_belt_id)
        REFERENCES curriculum_belts (id)
        ON DELETE CASCADE,
    -- Garante que não existam dois módulos com a mesma posição na mesma faixa/currículo
    CONSTRAINT uq_curriculum_belt_position
        UNIQUE (curriculum_belt_id, order_position)
);
-- Criação da tabela de técnicas
CREATE TABLE techniques (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    module_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    video_url VARCHAR(2048),
    order_position INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Módulos
    CONSTRAINT fk_technique_module
        FOREIGN KEY (module_id)
        REFERENCES modules (id)
        ON DELETE CASCADE,
    -- Garante que não existam duas técnicas com a mesma posição no mesmo módulo
    CONSTRAINT uq_module_technique_position
        UNIQUE (module_id, order_position)
);
-- Criação do tipo ENUM para o status do aprendizado
CREATE TYPE technique_progress_status AS ENUM (
    'NOT_STARTED',
    'LEARNING',
    'EXECUTES',
    'EXECUTES_WELL',
    'MASTERED'
);
-- Criação da tabela de progresso
CREATE TABLE student_technique_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    technique_id UUID NOT NULL,
    status technique_progress_status NOT NULL DEFAULT 'NOT_STARTED',
    score INTEGER NOT NULL DEFAULT 0,
    observations TEXT,
    last_evaluated_at TIMESTAMPTZ,
    evaluated_by UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Alunos
    CONSTRAINT fk_progress_student
        FOREIGN KEY (student_id)
        REFERENCES students (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para Técnicas
    CONSTRAINT fk_progress_technique
        FOREIGN KEY (technique_id)
        REFERENCES techniques (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para o Professor/Instrutor que avaliou
    CONSTRAINT fk_progress_evaluated_by
        FOREIGN KEY (evaluated_by)
        REFERENCES instructors (id)
        ON DELETE SET NULL, -- Se o professor sair do sistema, o histórico do aluno permanece
    -- Garante que a nota esteja estritamente entre 0 e 100
    CONSTRAINT chk_score_range
        CHECK (score >= 0 AND score <= 100),
    -- Impede duplicar a mesma técnica para o mesmo aluno
    CONSTRAINT uq_student_technique
        UNIQUE (student_id, technique_id)
);
-- Criação do tipo ENUM para o status do objetivo
CREATE TYPE goal_status AS ENUM ('OPEN', 'IN_PROGRESS', 'DONE');
-- Criação da tabela de objetivos
CREATE TABLE student_goals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status goal_status NOT NULL DEFAULT 'OPEN',
    target_date DATE,
    created_by UUID NOT NULL,    -- ID do usuário (sistema/professor/aluno) que criou a meta
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Alunos
    CONSTRAINT fk_goal_student
        FOREIGN KEY (student_id)
        REFERENCES students (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para quem criou o objetivo (tabela users)
    CONSTRAINT fk_goal_creator
        FOREIGN KEY (created_by)
        REFERENCES users (id)
        ON DELETE RESTRICT -- Impede apagar o usuário se houver metas registradas por ele
);
-- Criação da tabela de avaliações
CREATE TABLE evaluations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    instructor_id UUID NOT NULL,
    evaluation_date DATE NOT NULL DEFAULT CURRENT_DATE, -- Data em que o exame/avaliação ocorreu
    general_notes TEXT,                                 -- Considerações gerais do avaliador
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Alunos
    CONSTRAINT fk_evaluation_student
        FOREIGN KEY (student_id)
        REFERENCES students (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para Professores
    CONSTRAINT fk_evaluation_instructor
        FOREIGN KEY (instructor_id)
        REFERENCES instructors (id)
        ON DELETE RESTRICT -- Impede remover o professor se ele possuir avaliações assinadas
);
-- Criação da tabela do resultado da avaliação
CREATE TABLE evaluation_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evaluation_id UUID NOT NULL,
    technique_id UUID NOT NULL,
    score INTEGER NOT NULL DEFAULT 0, -- Nota de 0 a 100 para a técnica avaliada
    notes TEXT,                       -- Correções específicas do professor para este golpe
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para a Avaliação Mestre
    CONSTRAINT fk_item_evaluation
        FOREIGN KEY (evaluation_id)
        REFERENCES evaluations (id)
        ON DELETE CASCADE, -- Se a avaliação for apagada, limpa os itens dela
    -- Chave Estrangeira para as Técnicas
    CONSTRAINT fk_item_technique
        FOREIGN KEY (technique_id)
        REFERENCES techniques (id)
        ON DELETE RESTRICT, -- Impede apagar uma técnica do currículo se ela fez parte de um exame
    -- Garante que a nota esteja estritamente entre 0 e 100
    CONSTRAINT chk_item_score_range
        CHECK (score >= 0 AND score <= 100),
    -- Garante que a mesma técnica apareça apenas uma vez dentro desta avaliação
    CONSTRAINT uq_evaluation_technique
        UNIQUE (evaluation_id, technique_id)
);
-- Criação da tabela de aula
CREATE TABLE classes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    academy_id UUID NOT NULL,
    instructor_id UUID NOT NULL, -- Professor responsável por ministrar esta aula específica
    title VARCHAR(255) NOT NULL, -- Ex: "Treino Livre", "Fundamentos de Quedas"
    description TEXT,            -- Detalhes da aula ou posições passadas no dia
    class_date DATE NOT NULL DEFAULT CURRENT_DATE, -- Data em que a aula aconteceu
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Academias
    CONSTRAINT fk_class_academy
        FOREIGN KEY (academy_id)
        REFERENCES academies (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para Professores
    CONSTRAINT fk_class_instructor
        FOREIGN KEY (instructor_id)
        REFERENCES instructors (id)
        ON DELETE RESTRICT -- Impede apagar o professor se ele tiver dado aulas registradas
);
-- Criação do registro da aula, o que foi ensinado
CREATE TABLE class_techniques (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    class_id UUID NOT NULL,
    technique_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para a Aula/Treino
    CONSTRAINT fk_class_technique_class
        FOREIGN KEY (class_id)
        REFERENCES classes (id)
        ON DELETE CASCADE, -- Se a aula for deletada, remove o vínculo das técnicas ensinadas
    -- Chave Estrangeira para as Técnicas
    CONSTRAINT fk_class_technique_technique
        FOREIGN KEY (technique_id)
        REFERENCES techniques (id)
        ON DELETE RESTRICT, -- Impede apagar uma técnica do sistema se ela já foi ensinada em alguma aula
    -- Garante que a mesma técnica apareça apenas uma vez dentro de uma mesma aula
    CONSTRAINT uq_class_technique
        UNIQUE (class_id, technique_id)
);
-- Criação da tabela do registro presença
CREATE TABLE attendances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    class_id UUID NOT NULL,
    student_id UUID NOT NULL,
    present BOOLEAN NOT NULL DEFAULT TRUE, -- TRUE para presente, FALSE para falta
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para a Aula
    CONSTRAINT fk_attendance_class
        FOREIGN KEY (class_id)
        REFERENCES classes (id)
        ON DELETE CASCADE, -- Se a aula for deletada, o histórico de chamadas dela também é limpo
    -- Chave Estrangeira para o Aluno
    CONSTRAINT fk_attendance_student
        FOREIGN KEY (student_id)
        REFERENCES students (id)
        ON DELETE CASCADE, -- Se o aluno for removido, remove também suas presenças
    -- Garante que um aluno só receba uma chamada (chamada única) por aula
    CONSTRAINT uq_class_student_attendance
        UNIQUE (class_id, student_id)
);
-- Criação da tabela de observação sobre o aluno
CREATE TABLE student_notes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    instructor_id UUID NOT NULL, -- Identifica qual professor escreveu a nota
    note TEXT NOT NULL,          -- O conteúdo da anotação/observação
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Alunos
    CONSTRAINT fk_note_student
        FOREIGN KEY (student_id)
        REFERENCES students (id)
        ON DELETE CASCADE, -- Se o aluno for deletado, as anotações dele são limpas
    -- Chave Estrangeira para Professores
    CONSTRAINT fk_note_instructor
        FOREIGN KEY (instructor_id)
        REFERENCES instructors (id)
        ON DELETE RESTRICT -- Impede apagar o professor se ele tiver notas de alunos registradas
);
-- Cração da tabela de historico de faixas
CREATE TABLE belt_promotions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    from_belt_id UUID NOT NULL,          -- De qual faixa ele saiu (Ex: Branca)
    to_belt_id UUID NOT NULL,            -- Para qual faixa ele foi (Ex: Branca ou Azul)
    from_degree INTEGER NOT NULL DEFAULT 0, -- Grau antigo
    to_degree INTEGER NOT NULL DEFAULT 0,   -- Novo grau conquistado
    promotion_date DATE NOT NULL DEFAULT CURRENT_DATE, -- Data da graduação
    notes TEXT,                          -- Comentários sobre a entrega do grau/faixa
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Alunos
    CONSTRAINT fk_promotion_student
        FOREIGN KEY (student_id)
        REFERENCES students (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para a Faixa Antiga
    CONSTRAINT fk_promotion_from_belt
        FOREIGN KEY (from_belt_id)
        REFERENCES belts (id)
        ON DELETE RESTRICT,
    -- Chave Estrangeira para a Nova Faixa
    CONSTRAINT fk_promotion_to_belt
        FOREIGN KEY (to_belt_id)
        REFERENCES belts (id)
        ON DELETE RESTRICT,
    -- Validação: Impede que o aluno receba um grau menor ou igual na mesma faixa
    CONSTRAINT chk_degree_evolution
        CHECK (from_belt_id != to_belt_id OR to_degree > from_degree)
);