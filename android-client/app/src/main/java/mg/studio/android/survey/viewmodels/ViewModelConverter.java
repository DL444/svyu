package mg.studio.android.survey.viewmodels;

import java.util.ArrayList;

import javax.inject.Inject;

import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.SurveyModel;

public final class ViewModelConverter {

    @Inject
    public ViewModelConverter(QuestionViewModelSelector vmSelector) {
        this.vmSelector = vmSelector;
    }

    public SurveyModel getSurveyModel(ArrayList<IQuestionViewModel> viewModel) {
        SurveyModel model = new SurveyModel();
        model.setId("0");
        for (IQuestionViewModel vm : viewModel) {
            model.questions().add(vm.getModel());
        }
        return model;
    }

    public ArrayList<IQuestionViewModel> getSurveyViewModel(SurveyModel model) {
        ArrayList<IQuestionViewModel> vm = new ArrayList<>();
        for (IQuestion q : model.questions()) {
            vm.add(vmSelector.getViewModel(q));
        }
        return vm;
    }

    private final QuestionViewModelSelector vmSelector;
}
