-- enhancements

-- reference table for college under applicant table
CREATE TABLE college (
	collcode VARCHAR(8) NOT NULL PRIMARY KEY,
    collname VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- populate the college table
INSERT INTO college (collcode, collname)
SELECT DISTINCT collcode, collname
FROM applicant;

-- delete collname field in applicant
ALTER TABLE applicant
drop column collname;

-- make applicant.collcode a foreign key referencing college.collcode
ALTER TABLE applicant
ADD CONSTRAINT fk_applicant_college
foreign key (collcode)
references college(collcode);


-- create a reference table for program.progtype
create table program_type (
	progtype varchar(20) primary key
) engine=InnoDB;

-- populate program_type table
insert into program_type (progtype)
values ('FirstAward'), ('Renewal');

-- convert program.progtype datatype from enum to varchar so a ref table is possible
alter table program
modify progtype varchar(20) not null;

-- make program.progtype a foreign key referencing program_type.progtype
alter table program
add constraint fk_program_type
foreign key (progtype)
references program_type(progtype);

-- table that stores mappings between progtype and its required documents
create table program_document(
	progtype varchar(20) not null,
    doctype varchar(20) not null,
    
    primary key (progtype, doctype),
    foreign key(progtype)
    references program_type(progtype)
) engine=InnoDB;

-- populate program_document
insert into program_document (progtype, doctype)
values
    ('FirstAward', 'Identity'),
    ('FirstAward', 'Grades'),
    ('FirstAward', 'Income'),
    ('Renewal', 'Identity'),
    ('Renewal', 'Grades'),
    ('Renewal', 'Enrollment');

-- ref table for program agency
create table agency(
	agencode varchar(8) not null primary key,
    agenname varchar(120) not null
) engine=InnoDB;
 
-- populate agency table
insert into agency (agencode, agenname)
select distinct agencode, agenname
from program;

-- create table for firstaward programs (acting as a subtable to program)
create table first_award_program(
	progcode varchar(12) not null primary key,
    entrymin decimal(5,2),
    incomecp decimal(12, 2),
    
    foreign key(progcode)
    references program(progcode)
) engine = innodb;

-- create table for renewal programs
create table renewal_program(
	progcode varchar(12) not null primary key,
    minunits smallint,
    maxgpa decimal(4, 2),
    
    foreign key(progcode)
    references program(progcode)
) engine=innodb;

-- populate first award table
insert into first_award_program(progcode, entrymin, incomecp)
select progcode, entrymin, incomecp
from program
where progtype = 'FirstAward';

-- populate renewal table
insert into renewal_program(progcode, minunits, maxgpa)
select progcode, entrymin, incomecp
from program
where progtype = 'Renewal';

-- drop unnecessary columns from program table
alter table program
DROP COLUMN entrymin,
DROP COLUMN incomecp,
DROP COLUMN minunits,
DROP COLUMN maxgpa;