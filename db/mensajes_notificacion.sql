-- Insertar mensajes de notificación personalizados para cada estado
INSERT INTO mensaje_notificaciones (estado, mensaje) VALUES 
('PENDIENTE', 'Tu reporte ha sido recibido y se encuentra en espera de ser revisado.'),
('REVISION', 'Tu reporte está siendo revisado por un operador municipal.'),
('PROCESO', '¡Buenas noticias! Se ha asignado un técnico para atender tu reporte.'),
('RESUELTA', 'El problema reportado ha sido resuelto. ¡Gracias por contribuir a mejorar el distrito!'),
('CERRADA', 'Tu reporte ha sido cerrado satisfactoriamente.'),
('RECHAZADO_AUDITO', 'Tu reporte ha sido observado durante la auditoría. Por favor, revisa los comentarios.'),
('RECHAZADO', 'Lamentamos informarte que tu reporte ha sido rechazado');
