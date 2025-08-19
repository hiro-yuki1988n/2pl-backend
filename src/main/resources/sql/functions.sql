CREATE OR REPLACE FUNCTION insert_entry_fee_contributions(p_amount NUMERIC, p_year INT, p_month TEXT)
    RETURNS VOID AS $$
DECLARE
m RECORD;
BEGIN
FOR m IN SELECT id FROM members WHERE removed = FALSE
    LOOP
         INSERT INTO contributions (
    amount,
    date_paid,
    month,
    member_id,
    on_time,
    penalty_applied,
    penalty_amount,
    year,
    contribution_category,
    created_at,
    is_active,
    is_deleted
)
         VALUES (
             p_amount,
             NOW(),
             p_month,
             m.id,
             TRUE,
             FALSE,
             0.00,
             p_year,
             'ENTRY_FEE',
             NOW(),
             TRUE,
             FALSE
             );
END LOOP;
END;
$$ LANGUAGE plpgsql;

SELECT insert_entry_fee_contributions(10000, 2025, 'JANUARY');

-- ****************************PROCEDURE*********************

CREATE OR REPLACE PROCEDURE insert_entry_fee_contributions(p_amount NUMERIC, p_year INT, p_month TEXT)
    LANGUAGE plpgsql
AS $$
DECLARE
m RECORD;
BEGIN
FOR m IN SELECT id FROM members WHERE removed = FALSE
    LOOP
         INSERT INTO contributions (
    amount,
    date_paid,
    month,
    member_id,
    on_time,
    penalty_applied,
    penalty_amount,
    year,
    contribution_category,
    created_at,
    is_active,
    is_deleted
)
         VALUES (
             p_amount,
             NOW(),
             p_month,
             m.id,
             TRUE,
             FALSE,
             0.00,
             p_year,
             'ENTRY_FEE',
             NOW(),
             TRUE,
             FALSE
             );
END LOOP;
END;
$$;

-- ************************** INSERT SQL ******************************

INSERT INTO contributions (
    member_id,
    amount,
    contribution_category,
    month,
    on_time,
    date_paid,
    penalty_amount,
    penalty_applied,
    year,
    created_at,
    is_deleted,
    is_active)
values
    (2, 100000.00, 'ENTRY_FEE', 'MAY', true, '2023-05-31 17:46:35.664033', 0.00, false, 2023, '2025-01-31 17:46:35.664033', false, true),
    (3, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-02-28 17:46:35.664033', false, true),
    (4, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-02-28 17:46:35.664033', false, true),
    (5, 100000.00, 'ENTRY_FEE', 'MAY', true,  '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-02-28 17:46:35.664033', false, true),
    (6, 100000.00, 'ENTRY_FEE', 'MAY', true,  '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-02-28 17:46:35.664033', false, true),
    (7, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-02-28 17:46:35.664033', false, true),
    (8, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-02-28 17:46:35.664033', false, true),
    (9, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-02-28 17:46:35.664033', false, true),
    (10, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-02-28 17:46:35.664033', false, true),
    (11, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-02-28 17:46:35.664033', false, true),
    (12, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-01-31 17:46:35.664033', false, true),
    (13, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-01-31 17:46:35.664033', false, true),
    (14, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-01-31 17:46:35.664033', false, true),
    (15, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-01-31 17:46:35.664033', false, true),
    (16, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-01-31 17:46:35.664033', false, true),
    (17, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-01-31 17:46:35.664033', false, true),
    (18, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, FALSE, 2023, '2025-01-31 17:46:35.664033', false, true),
    (19, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-01-31 17:46:35.664033', false, true),
    (20, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-01-31 17:46:35.664033', false, true),
    (21, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-01-31 17:46:35.664033', false, true),
    (22, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-01-31 17:46:35.664033', false, true),
    (23, 100000.00, 'ENTRY_FEE', 'MAY', true, '2025-02-28 17:46:35.664033', 0.00, false, 2023, '2025-01-31 17:46:35.664033', false, true)


-- *********************************** SOCIAL FUNDS ***************************************
    INSERT INTO social_funds (
    member_id,
    amount,
    month,
    date_paid,
    created_at,
    is_deleted,
    is_active)
values
    (2, 10000.00, 'JULY', '2025-07-31 17:46:35.664033', '2025-07-31 17:46:35.664033', false, true),
    (3, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-02-28 17:46:35.664033', false, true),
    (4, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-02-28 17:46:35.664033', false, true),
    (5, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-02-28 17:46:35.664033', false, true),
    (6, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-02-28 17:46:35.664033', false, true),
    (7, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-02-28 17:46:35.664033', false, true),
    (8, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-02-28 17:46:35.664033', false, true),
    (9, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-02-28 17:46:35.664033', false, true),
    (10, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-02-28 17:46:35.664033', false, true),
    (11, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-02-28 17:46:35.664033', false, true),
    (12, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-01-31 17:46:35.664033', false, true),
    (13, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-01-31 17:46:35.664033', false, true),
    (14, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-01-31 17:46:35.664033', false, true),
    (15, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-01-31 17:46:35.664033', false, true),
    (16, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-01-31 17:46:35.664033', false, true),
    (17, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-01-31 17:46:35.664033', false, true),
    (18, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-01-31 17:46:35.664033', false, true),
    (19, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-01-31 17:46:35.664033', false, true),
    (20, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-01-31 17:46:35.664033', false, true),
    (21, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-01-31 17:46:35.664033', false, true),
    (22, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-01-31 17:46:35.664033', false, true),
    (23, 10000.00, 'JULY', '2025-02-28 17:46:35.664033', '2025-01-31 17:46:35.664033', false, true)