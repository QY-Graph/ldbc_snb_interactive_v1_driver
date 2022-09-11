-- variant (b): guaranteed that there is a path
SELECT
    person1Id AS 'person1Id',
    person2Id AS 'person2Id',
       useUntil AS 'useUntil'
FROM people4Hops,
    (
        SELECT :date_limit_long AS useUntil
    )
ORDER BY md5(concat(person1Id + 1, person2Id + 2))
LIMIT 500
