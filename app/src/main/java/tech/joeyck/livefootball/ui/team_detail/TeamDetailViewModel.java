package tech.joeyck.livefootball.ui.team_detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import tech.joeyck.livefootball.data.LiveFootballRepository;
import tech.joeyck.livefootball.data.database.TeamEntity;
import tech.joeyck.livefootball.data.network.ApiResponse;

public class TeamDetailViewModel extends ViewModel {

    private LiveData<ApiResponse<TeamEntity>> mTeam;

    TeamDetailViewModel(LiveFootballRepository repository, int id){
        mTeam = repository.getTeamById(id);
    }

    LiveData<ApiResponse<TeamEntity>> getTeam(){
        return mTeam;
    }

}
