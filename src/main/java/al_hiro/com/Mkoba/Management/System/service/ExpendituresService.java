package al_hiro.com.Mkoba.Management.System.service;

import al_hiro.com.Mkoba.Management.System.dto.ExpendituresDto;
import al_hiro.com.Mkoba.Management.System.entity.Member;
import al_hiro.com.Mkoba.Management.System.entity.Expenditures;
import al_hiro.com.Mkoba.Management.System.repository.ExpendituresRepository;
import al_hiro.com.Mkoba.Management.System.utils.PageableParam;
import al_hiro.com.Mkoba.Management.System.utils.Response;
import al_hiro.com.Mkoba.Management.System.utils.ResponsePage;
import al_hiro.com.Mkoba.Management.System.utils.Utils;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@GraphQLApi
@Log
public class ExpendituresService {

    @Autowired
    private ExpendituresRepository expendituresRepository;

    public Response<Expenditures> saveExpenditure(ExpendituresDto expendituresDto) {
        log.info("Saving expenditure");
        if (expendituresDto == null)
            return Response.warning(null, "Expenditure is required");

        Expenditures expenditures;
        if (expendituresDto.getId() != null) {
            Optional<Expenditures> optionalExpenditures = expendituresRepository.findById(expendituresDto.getId());
            if (optionalExpenditures.isEmpty())
                return Response.warning(null, "Expenditure not found");
            expenditures = optionalExpenditures.get();
            expenditures.update();
        } else {
            expenditures = new Expenditures();
        }

        if (expendituresDto.getAmount() == null)
            return Response.warning(null, "Amount is required");
        if (expendituresDto.getDateIssued() == null)
            return Response.warning(null, "Date of issuing expense is required");
        if (expendituresDto.getExpenseType() == null)
            return Response.warning(null, "Expense Type is required");
        if (expendituresDto.getDescription() == null)
            return Response.warning(null, "Member's Description is required");

        expenditures.setAmount(expendituresDto.getAmount());
        expenditures.setDateIssued(expendituresDto.getDateIssued());
        expenditures.setExpenseType(expendituresDto.getExpenseType());
        expenditures.setDescription(expendituresDto.getDescription());

        try {
            expendituresRepository.save(expenditures);
            return new Response<>(expenditures);
        } catch (Exception e) {
            e.printStackTrace();
            String message = Utils.getExceptionMessage(e);
            if (message.contains("description"))
                return new Response<>("Invalid Description");
            if (message.contains("amount"))
                return new Response<>("Invalid Amount");
            return new Response<>("Could not save Expenditure");
        }
    }

    public ResponsePage<Expenditures> getExpenditures(PageableParam pageableParam, Integer year) {
        log.info("Getting all Expenditures");
        return new ResponsePage<>(expendituresRepository.getExpenditures(pageableParam.getPageable(true), pageableParam.key(), year));
    }

    public Response<Expenditures> approveExpenditure(Long expenditureId) {
        log.info("Approving Expenditure");
        Optional<Expenditures> optionalExpenditures = expendituresRepository.findById(expenditureId);
        if (optionalExpenditures.isEmpty())
            return Response.warning(null, "Expenditure not found");
        Expenditures expenditures = optionalExpenditures.get();
        expenditures.setApproved(true);
        try {
            expendituresRepository.save(expenditures);
            return new Response<>(expenditures);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("Could not approve Expenditure");
        }
    }

    public Response<Expenditures> deleteExpenditure(Long expenditureId) {
        log.info("Deleting Expenditure");
        Optional<Expenditures> optionalExpenditures = expendituresRepository.findById(expenditureId);
        if (optionalExpenditures.isEmpty())
            return Response.warning(null, "Expenditure not found");
        Expenditures expenditures = optionalExpenditures.get();
        expenditures.delete();
        try {
            expenditures = expendituresRepository.save(expenditures);
            return new Response<>(expenditures);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("Could not delete Expenditure");
        }
    }

    public Double calculateTotalExpenditures() {
        log.info("Getting Group's total expenditures");
        Double totalExpenses = expendituresRepository.calculateTotalExpenditures();
        return totalExpenses != null ? totalExpenses : 0.0;
    }
}


