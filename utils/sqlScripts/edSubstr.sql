delimiter $$
DROP FUNCTION IF EXISTS edSubstr;
create function edSubstr(x varchar(100), target varchar(100))
returns boolean
deterministic
begin
    declare r boolean;
    if (length(x) < 6) then set r = edrec(x, target, 1);
    elseif (length(x) < 9) then set r = edrec(x, target, 2);
    else set r = edrec(x, target, 3);
    end if;
    return r;
end$$
delimiter ;


SELECT edSubstr('tlk', 'tl');
SELECT edSubstr('abcdefg', 'abcdef');
