use chinese;

drop table if exists `structure`;
drop table if exists `allography`;
drop table if exists `sinogram`;

create table `sinogram` ( -- caractère
	cp varchar(12) not null, -- insert codepoint as a string or a substitute.
    semantics varchar(256),
    consonants char,
    rhyme char,
    tone int,
    stroke tinyint,
    frequency tinyint, -- we further need to distingish between use in speech or use in other sinograms
    induced boolean not null, -- express whether that sinogram has been added properly or induced
    primary key (cp)
);

create table `allography` ( -- similitude
	cp varchar(12) not null,
    class int not null,
    foreign key (cp) references sinogram(cp),
    primary key (cp, class)
);

create table `structure` ( -- composition
	father_cp varchar(12) not null,
    son_cp varchar(12) not null,
    idc enum('⿰', '⿱', '⿲', '⿳', '⿴', '⿵', '⿶', '⿷', '⿸', '⿹', '⿺', '⿻'), -- null allowed if radical
    ordinal int not null,
    foreign key (father_cp) references sinogram(cp),
    foreign key (son_cp) references sinogram(cp),
    primary key (father_cp, son_cp, idc, ordinal)
);