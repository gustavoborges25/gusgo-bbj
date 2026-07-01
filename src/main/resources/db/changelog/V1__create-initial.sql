-- Criação da tabela de academias
CREATE TABLE academies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
-- insert default de academia
INSERT INTO academies (id, name, email, phone) VALUES
(
    '2863a5ec-7c67-4326-b4b2-92db272f5518',
    'Academia Default',
    'academiadefault@academiadefault.com',
    '44999999999'
);
-- Criação do tipo ENUM para as regras de acesso
CREATE TYPE user_role AS ENUM ('OWNER', 'INSTRUCTOR', 'STUDENT');
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
    -- Chave Estrangeira para Academias
    CONSTRAINT fk_user_academy
        FOREIGN KEY (academy_id)
        REFERENCES academies (id)
        ON DELETE CASCADE
);
-- insert default de usuário (senha admin123)
INSERT INTO users (id, academy_id, name, email, password_hash, role) VALUES
(
    'efd386cc-2e37-45b4-8786-32531867ca16',
    '2863a5ec-7c67-4326-b4b2-92db272f5518',
    'admin',
    'admin@teste.com',
    '$2a$10$NnsM904eEMWEhKuy5Ig5Z.imXqRcRWViV7oKNzVuhPavl8TSViBse',
    'OWNER'
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
    -- Chave Estrangeira para Usuários (Opcional)
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
-- Criação da tabela de módulos
CREATE TABLE modules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    order_position INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
-- insert default de módulos
INSERT INTO modules (name, description, order_position) VALUES
('Fundamentos', 'Base, postura, rolamentos, pegadas, fuga de quadril...', 1),
('Defesa Pessoal', 'Atenção, fugas, técnicas específicas...', 2),
('Quedas', 'Técnicas de projeção, entradas, contra golpes, como cair...', 3),
('Guardas', 'Conceitos de Guarda Fechada, Meia Guarda, De La Riva, Aranha, Laço...', 4),
('Raspagens', 'Tesourinha, balãozinho, safada', 5),
('Passagens', 'Toreando, over under, cruzando joelho...', 6),
('Controles', '100kg, montada, costas...', 7),
('Reposições', 'Saída do 100kg, saída da montada, saída das costas...  ', 8),
('Finalizações', 'Arm Lock, mata leão, ezequiel, loop chock...', 9);
-- Criação da tabela de técnicas
CREATE TABLE techniques (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    academy_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    video_url VARCHAR(2048),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Academias
    CONSTRAINT fk_technique_academy
        FOREIGN KEY (academy_id)
            REFERENCES academies (id)
            ON DELETE CASCADE
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
-- Criação da tabela que liga metodologia ao módulo
CREATE TABLE curriculum_modules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    curriculum_id UUID NOT NULL,
    module_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para metodologias
    CONSTRAINT fk_curriculum_modules_curriculum
        FOREIGN KEY (curriculum_id)
        REFERENCES curriculums (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para Faixas
    CONSTRAINT fk_curriculum_modules_module
        FOREIGN KEY (module_id)
        REFERENCES modules (id)
        ON DELETE RESTRICT,
    -- Impede duplicar a mesmo módulo dentro da mesma metodologia
    CONSTRAINT uq_curriculum_modules_curriculum_module
        UNIQUE (curriculum_id, module_id)
);
-- Criação da tabela que liga a metodologia/modulo com a faixa
CREATE TABLE curriculum_module_belts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    curriculum_module_id UUID NOT NULL,
    belt_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para metodologia/modulo
    CONSTRAINT fk_curriculum_module_belts_curriculum_module
        FOREIGN KEY (curriculum_module_id)
        REFERENCES curriculum_modules (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para faixa
    CONSTRAINT fk_curriculum_module_belts_belt
        FOREIGN KEY (belt_id)
        REFERENCES belts (id)
        ON DELETE RESTRICT,
    -- Impede duplicar mesma faixa dentro do mesmo metodologia/módulo
    CONSTRAINT uq_curriculum_module_belts_curriculum_module_belt
        UNIQUE (curriculum_module_id, belt_id)
);
-- Criação da tabela que liga a metodologia/módulo/faixa com a técnica
CREATE TABLE curriculum_module_belt_techniques (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    curriculum_module_belt_id UUID NOT NULL,
    technique_id UUID NOT NULL,
    required BOOLEAN NOT NULL DEFAULT FALSE,
    minimum_score INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para metodologia/módulo/faixa
    CONSTRAINT fk_curriculum_module_belt_techniques_curriculum_module_belt
        FOREIGN KEY (curriculum_module_belt_id)
        REFERENCES curriculum_module_belts (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para técnica
    CONSTRAINT fk_curriculum_module_belt_techniques_technique
        FOREIGN KEY (technique_id)
            REFERENCES techniques (id)
            ON DELETE CASCADE,
    -- Impede duplicar mesma técnica dentro da mesma metodologia/módulo/faixa
    CONSTRAINT uq_curriculum_module_belt_techniques_curriculum_module_belt_technique
        UNIQUE (curriculum_module_belt_id, technique_id)
);
-- Criação do tipo ENUM para o status do aprendizado
CREATE TYPE technique_progress_status AS ENUM (
    'NOT_STARTED',
    'LEARNING',
    'MASTERED'
);
-- Criação da tabela de progresso do Aluno por técnica
CREATE TABLE student_technique_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    technique_id UUID NOT NULL,
    status technique_progress_status NOT NULL DEFAULT 'NOT_STARTED',
    score INTEGER NOT NULL DEFAULT 0,
    observations TEXT,
    first_learned_at TIMESTAMPTZ,
    last_evaluated_at TIMESTAMPTZ,
    evaluated_by UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Alunos
    CONSTRAINT fk_student_technique_progress_student
        FOREIGN KEY (student_id)
        REFERENCES students (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para Técnicas
    CONSTRAINT fk_student_technique_progress_technique
        FOREIGN KEY (technique_id)
        REFERENCES techniques (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para o Professor/Instrutor que avaliou
    CONSTRAINT fk_student_technique_progress_evaluated_by
        FOREIGN KEY (evaluated_by)
        REFERENCES instructors (id)
        ON DELETE SET NULL, -- Se o professor sair do sistema, o histórico do aluno permanece
    -- Garante que a nota esteja estritamente entre 0 e 100
    CONSTRAINT chk_score_range
        CHECK (score >= 0 AND score <= 100),
    -- Impede duplicar a mesma técnica para o mesmo aluno
    CONSTRAINT uq_student_technique_progress_student_technique
        UNIQUE (student_id, technique_id)
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
    CONSTRAINT fk_evaluation_items_evaluation
        FOREIGN KEY (evaluation_id)
        REFERENCES evaluations (id)
        ON DELETE CASCADE, -- Se a avaliação for apagada, limpa os itens dela
    -- Chave Estrangeira para as Técnicas
    CONSTRAINT fk_evaluation_items_technique
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
-- Cração da tabela de historico de faixas
CREATE TABLE belt_promotions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    instructor_id UUID NOT NULL,
    from_belt_id UUID NOT NULL,          -- De qual faixa ele saiu (Ex: Branca)
    to_belt_id UUID NOT NULL,            -- Para qual faixa ele foi (Ex: Branca ou Azul)
    from_degree INTEGER NOT NULL DEFAULT 0, -- Grau antigo
    to_degree INTEGER NOT NULL DEFAULT 0,   -- Novo grau conquistado
    promotion_date DATE NOT NULL DEFAULT CURRENT_DATE, -- Data da graduação
    notes TEXT,                          -- Comentários sobre a entrega do grau/faixa
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Chave Estrangeira para Alunos
    CONSTRAINT fk_belt_promotions_student
        FOREIGN KEY (student_id)
        REFERENCES students (id)
        ON DELETE CASCADE,
    -- Chave Estrangeira para Alunos
    CONSTRAINT fk_belt_promotions_instructor
        FOREIGN KEY (instructor_id)
            REFERENCES instructors (id)
            ON DELETE CASCADE,
    -- Chave Estrangeira para a Faixa Antiga
    CONSTRAINT fk_belt_promotions_from_belt
        FOREIGN KEY (from_belt_id)
        REFERENCES belts (id)
        ON DELETE RESTRICT,
    -- Chave Estrangeira para a Nova Faixa
    CONSTRAINT fk_belt_promotions_to_belt
        FOREIGN KEY (to_belt_id)
        REFERENCES belts (id)
        ON DELETE RESTRICT
    );