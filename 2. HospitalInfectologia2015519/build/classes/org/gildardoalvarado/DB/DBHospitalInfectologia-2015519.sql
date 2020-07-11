create database DBHospitalInfectologia2015519;
use DBHospitalInfectologia2015519;
-- drop database DBHospitalInfectologia2015519;
-- ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'admin';

-- ---------------Medicos
-- select * FROM Medicos;
-- select * from Pacientes; 
-- select * from Especialidades;
-- select * from Cargos;
-- select * from Areas;
-- select * from telefonosMedico;
-- select * from contactourgencia;
-- select * from Horarios;
-- select * from MedicoEspecialidad;
 
CREATE TABLE Medicos(
	codigoMedico int auto_increment	not null,
    licenciaMedica integer not null,
    nombres varchar(100) not null,
    apellidos varchar(100) not null,
    horaEntrada	varchar (10) not null,
    horaSalida varchar (10) not null,
    turnoMaximo	int,
    sexo varchar(50) not null,
                          
	primary key PK_codigoMedico(codigoMedico)
);

CREATE TABLE Pacientes(
	codigoPaciente int auto_increment not null,
    DPI	varchar(100) not null,
    nombres varchar(100) not null,
    apellidos varchar(100) not null,
    fechaNacimiento	date not null,
    edad int ,
    direccion varchar(500) not null,
    ocupacion varchar(100) not null,
    sexo varchar(50) not null,
    
	primary key PK_codigoPaciente(codigoPaciente)
);

CREATE TABLE Areas(
	codigoArea int auto_increment not null,
    nombreArea varchar(100) not null,

	primary key PK_codigoArea(codigoArea)
    
);

CREATE TABLE Cargos(
	codigoCargo	int auto_increment not null,
    nombreCargo	varchar(100) not null,

	primary key PK_codigoCargo(codigoCargo)
);

CREATE TABLE Especialidades(
	codigoEspecialidad	int auto_increment not null,
    nombreEspecialidad 	varchar(100) not null,

	primary key PK_codigoEspecialidad(codigoEspecialidad)
);

CREATE TABLE ContactoUrgencia(
	codigoContactoUrgencia int auto_increment not null,
    nombres varchar(100) not null,
    apellidos varchar(100) not null,
    numeroContacto varchar(20) not null,
    codigoPaciente int not null,

	primary key PK_codigoContactoUrgencia(codigoContactoUrgencia),
    
-- FOREIGN KEY
	constraint FK_ContactoUrgencia_Pacientes
		foreign key(codigoPaciente)
			references Pacientes(codigoPaciente) on delete cascade on update cascade
		
);

CREATE TABLE ResponsableTurno(
	codigoResponsableTurno int auto_increment not null,
    nombresResponsable varchar(100) not null,
    apellidosResponsable varchar(100) not null,
    telefonoPersonal varchar(100) not null,
    codigoArea int not null,
    codigoCargo	int not null,

	primary key PK_codigoResponsableTurno(codigoResponsableTurno),
-- FOREGIN KEYS
	constraint FK_ResponsableTurno_Areas
		foreign key (codigoArea)
			references Areas(codigoArea) on delete cascade on update cascade,
	constraint FK_ResponsableTurno_Cargos
		foreign key (codigoCargo)
			references Cargos(codigoCargo) on delete cascade on update cascade
            
);

CREATE TABLE TelefonosMedico(
	codigoTelefonoMedico int auto_increment	not null,
    telefonoPersonal varchar(15) not null,
    telefonoTrabajo	varchar(15) not null,
    codigoMedico int not null,

	primary key PK_codigoTelefonoMedico(codigoTelefonoMedico),
-- FOREIGN KEY
	constraint FK_TelefonosMedico_Medicos
		foreign key(codigoMedico)
			references Medicos(codigoMedico) on delete cascade on update cascade
);

CREATE TABLE Horarios(
	codigoHorario int auto_increment not null,
    horarioInicio varchar (10) not null,
    horarioSalida varchar (10) not null,
    lunes boolean,
    martes	boolean,
    miercoles boolean,
    jueves boolean,
    viernes	boolean,

	primary key PK_codigoHorario(codigoHorario)
);

CREATE TABLE MedicoEspecialidad(
	codigoMedicoEspecialidad int auto_increment	not null,
    codigoMedico int not null,
    codigoEspecialidad int not null,
    codigoHorario int not null,

	primary key	PK_codigoMedicoEspecialidad(codigoMedicoEspecialidad),
-- FOREIGN KEYS
	constraint FK_MedicoEspecialidad_Medicos foreign key(codigoMedico) references Medicos(codigoMedico) on delete cascade on update cascade,
	constraint FK_MedicoEspecialidad_Especialidades foreign key(codigoEspecialidad) references Especialidades(codigoEspecialidad) on delete cascade on update cascade,
	constraint FK_MedicoEspecialidad_Horarios foreign key(codigoHorario) references Horarios(codigoHorario) on delete cascade on update cascade
);

CREATE TABLE Turnos(
	codigoTurno	int auto_increment	not null,
    fechaTurno	datetime not null,
    fechaCita datetime not null,
    valorCita decimal(10,2)	not null,
    codigoMedicoEspecialidad int not null,
    codigoPaciente int not null,
    codigoResponsableTurno int not null,

	primary key PK_codigoTurno(codigoTurno),
-- FOREIGN KEYS
	constraint 	FK_Turnos_Pacientes
		foreign key(codigoPaciente)
			references Pacientes(codigoPaciente) on delete cascade on update cascade,
	constraint	FK_Turnos_ResponsableTurno
		foreign key(codigoResponsableTurno)
			references ResponsableTurno(codigoResponsableTurno) on delete cascade on update cascade,
	constraint FK_Turnos_MedicoEspecialidad
		foreign key	(codigoMedicoEspecialidad)
			references MedicoEspecialidad(codigoMedicoEspecialidad) on delete cascade on update cascade
);

-- drop procedure sp_ProdecimientoJoin;

DELIMITER $$
CREATE PROCEDURE sp_ProdecimientoJoinMedicos()
	BEGIN 
		select * from Medicos P left Join TelefonosMedico C on P.codigoMedico = C.codigoMedico;
	END $$
DELIMITER $$

DELIMITER $$
CREATE PROCEDURE sp_ProdecimientoJoinPacientes()
	BEGIN 
		select * from Pacientes P left Join ContactoUrgencia C on P.codigoPaciente = C.codigoPaciente;
	END $$
DELIMITER $$

call sp_ProdecimientoJoin();

-- drop procedure sp_ProdecimientoJoinFinal;
DELIMITER $$
CREATE PROCEDURE sp_ProdecimientoJoinFinal( codigo int)
	BEGIN 
		select distinct(M.codigoMedico),M.LicenciaMedica,concat(M.nombres," ",M.apellidos)AS Nombres,M.horaEntrada,M.HoraSalida,M.TurnoMaximo,M.sexo,concat(H.viernes,H.jueves,H.miercoles,H.martes,H.lunes)as HorariosDeSemana,E.nombreEspecialidad,P.nombres,P.Apellidos,Co.numeroContacto,concat(R.nombresResponsable, " ",R.apellidosResponsable) as NombreResponsable,C.nombreCargo,A.nombreArea
        from Medicos M inner Join MedicoEspecialidad ME on M.codigoMedico = ME.codigoMedico 
        inner join Horarios H inner join MedicoEspecialidad on H.CodigoHorario = ME.CodigoHorario
        inner join Especialidades E on ME.codigoEspecialidad = E.codigoEspecialidad
        inner join Turnos T on ME.codigoMedicoEspecialidad = T.codigoMedicoEspecialidad
        inner Join ResponsableTurno R on R.codigoResponsableTurno = T.codigoResponsableTurno
        inner join Pacientes P on P.codigoPaciente = T.codigoPaciente
        left join ContactoUrgencia Co on Co.codigoPaciente = P.codigoPaciente
        inner join Areas A on A.codigoArea = R.codigoArea
        inner join Cargos C on C.codigoCargo = R.codigoCargo where codigo = M.codigoMedico;
        
	END $$
DELIMITER $$

call sp_ProdecimientoJoinFinal(1);

-- ------------------------------ Procedimientos Almacenados de Medicos 
-- -- Agregar -- --
Delimiter $$
CREATE PROCEDURE sp_AgregarMedico(licenciaMedica int, nombres varchar(100), apellidos varchar(100), horaEntrada varchar (10), horaSalida varchar (10), sexo varchar(50))
		BEGIN
			insert into Medicos(licenciaMedica, nombres, apellidos, horaEntrada, horaSalida, sexo)
		values (licenciaMedica, nombres, apellidos, horaEntrada, horaSalida, sexo);
    END$$
Delimiter ;

-- Eliminar
Delimiter $$
CREATE PROCEDURE sp_EliminarMedico(codigo int)
		BEGIN
			delete from Medicos where codigo = codigoMedico;			
		END$$
Delimiter ;

-- Editar
Delimiter $$
CREATE PROCEDURE sp_EditarMedico(licenciaMedica2 int, nombres2 varchar(100), apellidos2 varchar(100), horaEntrada2 varchar (10), horaSalida2 varchar (10),sexo2 varchar(50),codigo int)
		BEGIN
		update Medicos set  
			licenciaMedica = licenciaMedica2, 
			nombres = nombres2,
			apellidos = apellidos2, 
			horaEntrada = horaEntrada2, 
			horaSalida = horaSalida2, 
			sexo = sexo2 where codigo = codigoMedico;
    END$$
Delimiter ;

-- Enlistar    
Delimiter $$
CREATE PROCEDURE sp_EnlistarMedicos()
		BEGIN
			select 
			codigoMedico,
			licenciaMedica, 
			nombres, 
			apellidos, 
			horaEntrada, 
			horaSalida, 
			turnoMaximo, 
			sexo from Medicos;
	END$$
Delimiter ;

-- Buscar    
Delimiter $$
CREATE PROCEDURE sp_BuscarMedico(codigo int)
		BEGIN
			select 
			Medicos.codigoMedico,
			Medicos.licenciaMedica, 
			Medicos.nombres, 
			Medicos.apellidos, 
			Medicos.horaEntrada, 
			Medicos.horaSalida, 
			Medicos.turnoMaximo, 
			sexo from Medicos where codigoMedico = codigo;
    END$$
Delimiter ;
-- call sp_AgregarPaciente("1231231231231","h","r","2019/05/6","2","2","m");
-- Agregar
Delimiter $$
CREATE PROCEDURE sp_AgregarPaciente(DPI varchar(100), nombres varchar(100), apellidos varchar(100), fechaNacimiento date,direccion varchar(500), ocupacion varchar(100), sexo varchar(50))
	BEGIN
		insert into Pacientes(DPI, nombres, apellidos, fechaNacimiento, direccion, ocupacion, sexo)
			values(DPI, nombres, apellidos, fechaNacimiento, direccion, ocupacion, sexo);
    END$$
Delimiter ;

-- Eliminar  
Delimiter $$
CREATE PROCEDURE sp_EliminarPaciente(codigo int)
	BEGIN
		delete from Pacientes where codigo = codigoPaciente;
    END$$
Delimiter ;

-- Editar
Delimiter $$
CREATE PROCEDURE sp_EditarPaciente(DPI2 varchar(100), nombres2 varchar(100), apellidos2 varchar(100), direccion2 varchar(500), ocupacion2 varchar(100), sexo2 varchar(50), id int)
	BEGIN
		update Pacientes set
		DPI = DPI2, 
        nombres = nombres2, 
        apellidos = apellidos2, 
        direccion = direccion2, 
        ocupacion = ocupacion2, 
        sexo = sexo2 where id = codigoPaciente;
    END$$
Delimiter ;

-- Enlistar
Delimiter $$
CREATE PROCEDURE sp_EnlistarPacientes()
	BEGIN
		select 
        codigoPaciente,
        DPI, 
        nombres, 
        apellidos, 
        fechaNacimiento, 
        edad, direccion,
        ocupacion, 
        sexo from Pacientes;
    END$$
Delimiter ;

-- Buscar    
Delimiter $$
CREATE PROCEDURE sp_BuscarPaciente(codigo int)
	BEGIN
		select 
		codigoPaciente,
		DPI, 
        nombres, 
        apellidos, 
        fechaNacimiento, 
        edad, direccion,
        ocupacion, 
        sexo from Pacientes where codigo = codigoPaciente;
    END$$
Delimiter ;

----- -------------------- Procedimientos Almacenados de Areas

-- ---- Agregar
Delimiter $$
CREATE PROCEDURE sp_AgregarArea(nombreArea varchar(100))
		BEGIN
			insert into Areas(Areas.nombreArea)
				values(nombreArea);
		END$$
Delimiter ; 

-- Eliminar
Delimiter $$
CREATE PROCEDURE sp_EliminarArea(codigo int)
		BEGIN
			delete from Areas where codigo = codigoArea;
		END$$
Delimiter ;

-- Editar
Delimiter $$
CREATE PROCEDURE sp_EditarArea(nombreArea2 varchar(100), codigo int)
		BEGIN
			update Areas set
				nombreArea = nombreArea2 where codigo = codigoArea;
		END$$
Delimiter ;

-- Enlistar
Delimiter $$
CREATE PROCEDURE sp_EnlistarAreas()
		BEGIN
			select codigoArea, nombreArea from Areas;
		END$$
Delimiter ;

-- Buscar  
Delimiter $$
CREATE PROCEDURE sp_BuscarArea(id int)
		BEGIN
			select codigoArea, nombreArea from Areas where id = codigoArea;
		END$$
Delimiter ;


-- --------------------------- Procedimientos Almacenados Cargos
-- Agregar
Delimiter $$
CREATE PROCEDURE sp_AgregarCargo(nombreCargo varchar(100))
	BEGIN
		insert into Cargos(Cargos.nombreCargo)
			values(nombreCargo);
    END$$
Delimiter ; 

-- Eliminar    
Delimiter $$
CREATE PROCEDURE sp_EliminarCargo(codigo int)
	BEGIN
		delete from Cargos where codigo = codigoCargo;
    END$$
Delimiter ;

-- Editar    
Delimiter $$
CREATE PROCEDURE sp_EditarCargo(nombreCargo2 varchar(100), codigo int)
	BEGIN
		update Cargos set
			nombreCargo = nombreCargo2 where codigo = codigoCargo;
    END$$
Delimiter ;

-- Enlistar    
Delimiter $$
CREATE PROCEDURE sp_EnlistarCargos()
	BEGIN
		select codigoCargo, nombreCargo from Cargos;
    END$$
Delimiter ;

-- Buscar    
Delimiter $$
CREATE PROCEDURE sp_BuscarCargo(codigo int)
	BEGIN
		select codigoCargo, nombreCargo from Cargos where codigo = codigoCargo;
    END$$
Delimiter ;

-- ----------------------------- Procedimientos Almacenados de Especialidades
-- -------- Agregar
Delimiter $$
CREATE PROCEDURE sp_AgregarEspecialidad(nombreEspecialidad varchar(100))
	BEGIN
		insert into Especialidades (Especialidades.nombreEspecialidad)
			values(nombreEspecialidad);
    END$$
Delimiter ;

-- Eliminar
Delimiter $$
CREATE PROCEDURE sp_EliminarEspecialidad(codigo int)
	BEGIN
		 delete from Especialidades where codigo = codigoEspecialidad;
    END$$
Delimiter ;

-- Editar
Delimiter $$
CREATE PROCEDURE sp_EditarEspecialidad(nombreEspecialidad2 varchar(100), codigo int)
	BEGIN
		update Especialidades set 
        Especialidades.nombreEspecialidad = nombreEspecialidad2 where Especialidades.codigoEspecialidad = codigo;
    END$$
Delimiter ;

-- Enlistar
Delimiter $$
CREATE PROCEDURE sp_EnlistarEspecialidades()
	BEGIN
		select 
        codigoEspecialidad,
        nombreEspecialidad
        from Especialidades;        
    END$$
Delimiter ;

-- Buscar
Delimiter $$
CREATE PROCEDURE sp_BuscarEspecialidad(codigo int)
	BEGIN
		select 
        codigoEspecialidad,
        nombreEspecialidad
        from Especialidades where codigoEspecialidad = codigo;
    END$$
Delimiter ;


-- Procedimientos Almacenados de horarios
-- --------------- Agregar
Delimiter $$
CREATE PROCEDURE sp_AgregarHorario(horarioInicio varchar (10), horarioSalida varchar (10), lunes boolean, martes boolean, miercoles boolean, jueves boolean, viernes boolean)
	BEGIN 
		insert into Horarios(horarioInicio, horarioSalida, lunes, martes, miercoles, jueves, viernes)
			values(horarioInicio, horarioSalida, lunes, martes, miercoles, jueves, viernes);
    END$$
Delimiter ;

-- Eliminar
Delimiter $$
CREATE PROCEDURE sp_EliminarHorario(codigo int)
	BEGIN 
		delete from Horarios where codigoHorario = codigo;
    END$$
Delimiter ;

-- Editar
Delimiter $$
CREATE PROCEDURE sp_EditarHorario(horarioInicio2 varchar (10), horarioSalida2 varchar (10), lunes2 boolean, martes2 boolean, miercoles2 boolean, jueves2 boolean, viernes2 boolean, codigo int)
	BEGIN 
		update Horarios set
        horarioInicio = horarioInicio2, 
        horarioSalida = horarioSalida2, 
        lunes = lunes2, 
        martes = martes2, 
        miercoles = miercoles2, 
        jueves = jueves2,  
        viernes = viernes2 where codigoHorario = codigo;
    END$$
Delimiter ;

-- Enlistar
Delimiter $$
CREATE PROCEDURE sp_EnlistarHorarios()
	BEGIN 
		select 
		codigoHorario,
		horarioInicio, 
        horarioSalida, 
        lunes, 
        martes, 
        miercoles,
        jueves, 
        viernes from Horarios;
    END$$
Delimiter ;

-- Buscar
Delimiter $$
CREATE PROCEDURE sp_BuscarHorario(codigo int)
	BEGIN 
		select 
		codigoHorario,
		horarioInicio, 
        horarioSalida, 
        lunes, 
        martes, 
        miercoles,
        jueves, 
        viernes from Horarios where codigoHorario = codigo;
    END$$
Delimiter ;

-- Procedimientos Almacenados de contacto Urgencia
-- --------------- Agregar
Delimiter $$
CREATE PROCEDURE sp_AgregarContactoUrgencia(nombres varchar(100), apellidos varchar(100), numeroContacto varchar(20), codigoPaciente int)
	BEGIN
		insert into ContactoUrgencia(ContactoUrgencia.nombres, ContactoUrgencia.apellidos, ContactoUrgencia.numeroContacto, ContactoUrgencia.codigoPaciente)
			values(nombres, apellidos, numeroContacto, codigoPaciente);
    END$$
Delimiter ;
-- Eliminar
Delimiter $$
CREATE PROCEDURE sp_EliminarContactoUrgencia(codigo int)
	BEGIN
		delete from ContactoUrgencia where codigo = codigoContactoUrgencia;
    END$$
Delimiter ;

-- Editar
Delimiter $$
CREATE PROCEDURE sp_EditarContactoUrgencia(nombres2 varchar(100), apellidos2 varchar(100), numeroContacto2 varchar(20), codigoPaciente2 int, codigo int)
	BEGIN
		update ContactoUrgencia set
        nombres = nombres2, 
        apellidos = apellidos2,
        numeroContacto = numeroContacto2, 
        codigoPaciente = codigoPaciente2 where codigo = codigoContactoUrgencia;
    END$$
Delimiter ;

-- Enlistar
Delimiter $$
CREATE PROCEDURE sp_EnlistarContactoUrgencia()
	BEGIN
		select 
        codigoContactoUrgencia, 
        nombres, 
        apellidos,
        numeroContacto, 
        codigoPaciente from ContactoUrgencia;
    END$$
Delimiter ;

-- Buscar
Delimiter $$
CREATE PROCEDURE sp_BuscarContactoUrgencia(codigo int)
	BEGIN
    select 
        codigoContactoUrgencia, 
        nombres, 
        apellidos,
        numeroContacto, 
        codigoPaciente from ContactoUrgencia where codigo = codigoContactoUrgencia;
    END$$
Delimiter ;

-- ------- Procedimientos Almacenados de responsable de turno
-- ----------- Agregar
Delimiter $$
CREATE PROCEDURE sp_AgregarResponsableTurno(nombresResponsable varchar(100), apellidosResponsable varchar(100), telefonoPersonal varchar(100), codigoArea int, codigoCargo int)
	BEGIN
		insert into ResponsableTurno(ResponsableTurno.nombresResponsable, ResponsableTurno.apellidosResponsable, ResponsableTurno.telefonoPersonal, ResponsableTurno.codigoArea, ResponsableTurno.codigoCargo)
			values(nombresResponsable, apellidosResponsable, telefonoPersonal, codigoArea, codigoCargo);
    END$$
Delimiter ;

-- Eliminar
Delimiter $$
CREATE PROCEDURE sp_EliminarResponsableTurno(codigo int)
	BEGIN
		Delete from ResponsableTurno where codigo = codigoResponsableTurno;
    END$$
Delimiter ;

-- Editar
Delimiter $$
CREATE PROCEDURE sp_EditarResponsableTurno(nombresResponsable2 varchar(100), apellidosResponsable2 varchar(100), telefonoPersonal2 varchar(100), codigoArea2 int, codigoCargo2 int, codigo int)
	BEGIN
		update ResponsableTurno set
        nombresResponsable = nombresResponsable2, 
        apellidosResponsable = apellidosResponsable2, 
        telefonoPersonal = telefonoPersonal2, 
        codigoArea = codigoArea2,
        codigoCargo = codigoCargo2 where codigo = codigoResponsableTurno;
    END$$
Delimiter ;

-- Enlistar
Delimiter $$
CREATE PROCEDURE sp_EnlistarResponsableTurno()
	BEGIN
		select codigoResponsableTurno,
        nombresResponsable, 
        apellidosResponsable, 
        telefonoPersonal, 
        codigoArea, 
        codigoCargo from ResponsableTurno;
    END$$
Delimiter ;

-- Buscar
Delimiter $$
CREATE PROCEDURE sp_BuscarResponsableTurno(codigo int)
	BEGIN
		select codigoResponsableTurno,
        nombresResponsable, 
        apellidosResponsable, 
        telefonoPersonal, 
        codigoArea, 
        codigoCargo from ResponsableTurno where codigo = codigoResponsableTurno;
    END$$
Delimiter ;

-- ----------- Procedimientos Almacenados Telefonos Medico
-- ------------- Agregar
Delimiter $$
CREATE PROCEDURE sp_AgregarTelefonoMedico(telefonoPersonal varchar(15), telefonoTrabajo varchar(15), codigoMedico int)
	BEGIN
		insert into TelefonosMedico(TelefonosMedico.telefonoPersonal, TelefonosMedico.telefonoTrabajo, TelefonosMedico.codigoMedico)
			values(telefonoPersonal, telefonoTrabajo, codigoMedico);
    END$$
Delimiter ;

-- Eliminar
Delimiter $$
CREATE PROCEDURE sp_EliminarTelefonoMedico(codigo int)
	BEGIN
		Delete from TelefonosMedico where codigo = codigoTelefonoMedico;
    END$$
Delimiter ;

-- Editar
Delimiter $$
CREATE PROCEDURE sp_EditarTelefonoMedico(telefonoPersonal2 varchar(15), telefonoTrabajo2 varchar(15), codigoMedico2 int, codigo int)
	BEGIN
		update TelefonosMedico set
			telefonoPersonal = telefonoPersonal2,
            telefonoTrabajo = telefonoTrabajo2, 
            codigoMedico = codigoMedico2 where codigo = codigoTelefonoMedico;
    END$$
Delimiter ;

-- Enlistar
Delimiter $$
CREATE PROCEDURE sp_EnlistarTelefonosMedico()
	BEGIN
		select codigoTelefonoMedico, 
        telefonoPersonal,
        telefonoTrabajo, 
        codigoMedico from TelefonosMedico;
    END$$
Delimiter ;

-- Buscar
Delimiter $$
CREATE PROCEDURE sp_BuscarTelefonoMedico(codigo int)
	BEGIN
		select codigoTelefonoMedico, 
        telefonoPersonal,
        telefonoTrabajo, 
        codigoMedico from TelefonosMedico where codigo = codigoTelefonoMedico;
    END$$
Delimiter ;

-- Procedimientos Almacenados Medico Especialidad
-- ----------Agregar
Delimiter $$
CREATE PROCEDURE sp_AgregarMedicoEspecialidad(codigoMedico int, codigoEspecialidad int, codigoHorario int)
	BEGIN
		insert into MedicoEspecialidad(MedicoEspecialidad.codigoMedico, MedicoEspecialidad.codigoHorario, MedicoEspecialidad.codigoEspecialidad)
			values(codigoMedico, codigoEspecialidad, codigoHorario);
    END$$
Delimiter ;

-- Eliminar
Delimiter $$
CREATE PROCEDURE sp_EliminarMedicoEspecialidad(codigo int)
	BEGIN
		delete  from MedicoEspecialidad where codigo = codigoMedicoEspecialidad;
    END$$
Delimiter ;

-- Editar
Delimiter $$
CREATE PROCEDURE sp_EditarMedicoEspecialidad(codigoMedico2 int, codigoEspecialidad2 int, codigoHorario2 int, codigo int)
	BEGIN
		update MedicoEspecialidad set
		MedicoEspecialidad.codigoMedico = codigoMedico2,
        MedicoEspecialidad.codigoEspecialidad = codigoEspecialidad2,
        MedicoEspecialidad.codigoHorario = codigoHorario2 where codigo = codigoMedicoEspecialidad;
    END$$
Delimiter ;

-- Enlistar
Delimiter $$
CREATE PROCEDURE sp_EnlistarMedicoEspecialidad()
	BEGIN
		select codigoMedicoEspecialidad, 
        codigoMedico, 
        codigoEspecialidad,
        codigoHorario from MedicoEspecialidad;
    END$$
Delimiter ;

-- Buscar
Delimiter $$
CREATE PROCEDURE sp_BuscarMedicoEspecialidad(codigo int)
	BEGIN
		select codigoMedicoEspecialidad, 
        codigoMedico, 
        codigoEspecialidad,
        codigoHorario from MedicoEspecialidad where codigo = codigoMedicoEspecialidad;
    END$$
Delimiter ;
-- ---------------------- Procedimientos Almacenados de turnos
-- ----------------  Agregar
Delimiter $$
CREATE PROCEDURE sp_AgregarTurno(fechaTurno date, fechaCita date, valorCita decimal(10,2), codigoMedicoEspecialidad int, codigoPaciente int, codigoResponsableTurno int)
	BEGIN
		insert into Turnos(fechaTurno, fechaCita, valorCita, codigoMedicoEspecialidad, codigoPaciente, codigoResponsableTurno)
			values(fechaTurno, fechaCita, valorCita, codigoMedicoEspecialidad, codigoPaciente, codigoResponsableTurno);
    END$$
Delimiter ;

-- Eliminar
Delimiter $$
CREATE PROCEDURE sp_ElimiarTurno(codigo int)
	BEGIN
		Delete from Turnos where codigo = codigoTurno;
    END$$
Delimiter ;

-- Editar
Delimiter $$
CREATE PROCEDURE sp_EditarTurno(fechaTurno2 date, fechaCita2 date, valorCita2 decimal(10,2), codigoMedicoEspecialidad2 int, codigoPaciente2 int, codigoResponsableTurno2 int, codigo int)
	BEGIN
		update Turnos set
        fechaTurno = fechaTurno2,
        fechaCita = fechaCita2,
        valorCita = valorCita2, 
        codigoMedicoEspecialidad = codigoMedicoEspecialidad2, 
        codigoPaciente = codigoPaciente2,
        codigoResponsableTurno = codigoResponsableTurno2 where codigo = codigoTurno;
    END$$
Delimiter ;

-- Enlistar
Delimiter $$
CREATE PROCEDURE sp_EnlistarTurnos()
	BEGIN
		select codigoTurno, 
        fechaTurno, 
        fechaCita,
        valorCita,
        codigoMedicoEspecialidad,
        codigoPaciente, 
        codigoResponsableTurno from Turnos;
    END$$
Delimiter ;

-- Buscar
Delimiter $$
CREATE PROCEDURE sp_BuscarTurno(codigo int)
	BEGIN
		select codigoTurno, 
        fechaTurno, 
        fechaCita,
        valorCita,
        codigoMedicoEspecialidad,
        codigoPaciente, 
        codigoResponsableTurno from Turnos where codigoTurno = codigo;
    END$$
Delimiter ;

-- Funciones -------------------------------------------------------------
-- Funcion de edad -------------------------------------------------------
-- drop function fn_CalcularEdad;

/*Delimiter $$
CREATE FUNCTION fn_CalcularEdad (fecha date)
RETURNS int
READS SQL DATA DETERMINISTIC

BEGIN
DECLARE edad int ;
set edad = 0;
SET edad = TIMESTAMPDIFF(YEAR, fecha, CURDATE());

RETURN edad;
END$$
Delimiter ;
select fn_CalcularEdad("2004-05-06");*/

-- triggers ----------------------------------------------------------------
-- trigger de edad --------------------------------
Delimiter $$
CREATE TRIGGER tr_Edad_De_Paciente
BEFORE INSERT ON Pacientes
	FOR EACH ROW
		BEGIN
			set new.edad = TIMESTAMPDIFF(YEAR, new.fechaNacimiento, CURDATE());
		END$$
Delimiter ;

-- trigger para actualizar la edad ---------------

Delimiter $$
CREATE TRIGGER tr_Actualizar_Edad
BEFORE UPDATE ON Pacientes
	FOR EACH ROW
	BEGIN
		set new.edad = TIMESTAMPDIFF(YEAR, new.fechaNacimiento, CURDATE());
	END$$
Delimiter ;

Delimiter $$
create function fn_TurnoMaximo(lunes boolean, martes boolean, miercoles boolean, jueves boolean, viernes boolean) returns int 
	READS SQL DATA DETERMINISTIC
    BEGIN
		declare resultado int;
        set resultado = 0;
        if lunes = true then 
			set resultado = resultado + 1;
            else 
            set resultado = resultado + 0;
		end if;
        
        if martes = true then 
			set resultado = resultado + 1;
            else 
            set resultado = resultado + 0;
		end if;
        
        if miercoles = true then 
			set resultado = resultado + 1;
            else 
            set resultado = resultado + 0;
		end if;
        
        if jueves = true then 
			set resultado = resultado + 1;
            else 
            set resultado = resultado + 0;
		end if;
        
        if viernes = true then 
			set resultado = resultado + 1;
            else 
            set resultado = resultado + 0;
		end if;
		return resultado;
    END$$
Delimiter ;

Delimiter $$
	create trigger tr_Actualizar_TurnoMximo__Medico
		After Insert ON medicoespecialidad
			FOR EACH ROW
				BEGIN
                Declare turnos int default 0;
                declare lunes boolean;
                declare martes boolean;
                declare miercoles boolean;
                declare jueves boolean;
                declare viernes boolean;
                
                set lunes = (select horarios.lunes from Horarios where horarios.codigoHorario = new.codigoHorario);
                set martes = (select horarios.martes from Horarios where horarios.codigoHorario = new.codigoHorario);
                set miercoles = (select horarios.miercoles from Horarios where horarios.codigoHorario = new.codigoHorario);
                set jueves = (select horarios.jueves from Horarios where horarios.codigoHorario = new.codigoHorario);
                set viernes = (select horarios.viernes from Horarios where horarios.codigoHorario = new.codigoHorario);

					update Medicos set medicos.turnoMaximo = fn_TurnoMaximo(lunes, martes, miercoles, jueves, viernes) where codigoMedico = new.codigoMEdico;
				END$$
Delimiter ;