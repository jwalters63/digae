package com.digae.sga.service;

import com.digae.sga.entity.Supervision;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PdfExportService {

    @Async
    public void exportarSupervisionPdf(Supervision supervision) {
        try {

            System.out.println("Iniciando generación asíncrona de PDF para supervisión ID: " + supervision.getId());
            Thread.sleep(2000);
            System.out.println("PDF generado exitosamente para supervisión ID: " + supervision.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error en la generación asíncrona de PDF");
        }
    }
}
