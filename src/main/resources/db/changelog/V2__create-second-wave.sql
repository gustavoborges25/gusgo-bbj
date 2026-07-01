-- Esse sql será feito em uma segunda etapa, não vai ser executado em MVP.
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
