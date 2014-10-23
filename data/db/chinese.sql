use chinese;

drop table if exists `structure`;
drop table if exists `allography`;
drop table if exists `sinogram`;

create table sinogram ( -- caractère
	cp varchar(12) not null, -- insérer le point de code en chaîne de caractère ou un substitut.
    semantics varchar(256),
    consonants char,
    rhyme char,
    tone int,
    stroke tinyint,
    frequency tinyint,
    primary key (cp)
);

create table allography ( -- similitude
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