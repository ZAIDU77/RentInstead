USE rentinstead;

INSERT INTO items
(owner_id, title, description, price_per_day, image_url, available)
SELECT
    user_id,
    'Student Bicycle',
    'Good condition bicycle available for student rental. Suitable for daily college travel.',
    100.00,
    NULL,
    TRUE
FROM users
WHERE account_status = 'ACTIVE'
ORDER BY user_id
LIMIT 1;
