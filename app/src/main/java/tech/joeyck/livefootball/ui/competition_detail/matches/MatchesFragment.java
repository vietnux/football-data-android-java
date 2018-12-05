package tech.joeyck.livefootball.ui.competition_detail.matches;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import tech.joeyck.livefootball.R;
import tech.joeyck.livefootball.data.database.MatchEntity;
import tech.joeyck.livefootball.data.database.MatchesResponse;
import tech.joeyck.livefootball.data.network.ApiResponseObserver;
import tech.joeyck.livefootball.ui.competition_detail.BaseListFragment;
import tech.joeyck.livefootball.ui.competition_detail.CompetitionActivity;
import tech.joeyck.livefootball.utilities.InjectorUtils;

public class MatchesFragment extends BaseListFragment implements MatchesAdapter.MatchesAdapterOnItemClickHandler {

    public static final String FRAGMENT_TAG = "MatchesFragment";
    public static final String TEAM_ID_EXTRA = "TEAM_ID_EXTRA";

    private RecyclerView mRecyclerView;
    private MatchesViewModel mViewModel;
    private MatchesAdapter mMatchesAdapter;

    public static MatchesFragment newInstance(int teamId){
        MatchesFragment fragment = new MatchesFragment();
        Bundle args = new Bundle();
        args.putInt(TEAM_ID_EXTRA, teamId);
        fragment.setArguments(args);
        return fragment;
    }

    public static MatchesFragment newInstance(int competitionId,String competitionName,int matchDay){
        MatchesFragment fragment = new MatchesFragment();
        Bundle args = new Bundle();
        args.putInt(CompetitionActivity.COMPETITION_ID_EXTRA, competitionId);
        args.putInt(CompetitionActivity.COMPETITION_MATCHDAY_EXTRA, matchDay);
        args.putString(CompetitionActivity.COMPETITION_NAME_EXTRA, competitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int competitionId = getArguments().getInt(CompetitionActivity.COMPETITION_ID_EXTRA, -1);
        int matchday = getArguments().getInt(CompetitionActivity.COMPETITION_MATCHDAY_EXTRA, -1);
        int teamId = getArguments().getInt(TEAM_ID_EXTRA,-1);

        View view = super.onCreateView(inflater,container,savedInstanceState,false,teamId > 0);

        MatchesViewModelFactory factory = InjectorUtils.provideMatchesViewModelFactory(getActivity().getApplicationContext(),competitionId,matchday,teamId);
        mViewModel = factory.create(MatchesViewModel.class);

        mMatchesAdapter = new MatchesAdapter(getActivity(), this);
        setAdapter(mMatchesAdapter);

        //If we get a team Id we get matches for the team, if not we get matches from the competition
        if(teamId > 0 ){
            mViewModel.getMatchesForTeam().observe(this, responseObserver);
        }else{
            mViewModel.getMatchesForCompetition().observe(this, responseObserver);
        }
        showLoading();
        return view;
    }

    private ApiResponseObserver<MatchesResponse> responseObserver = new ApiResponseObserver<MatchesResponse>(new ApiResponseObserver.ChangeListener<MatchesResponse>() {
        @Override
        public void onSuccess(MatchesResponse responseBody) {
            bindMatchesToUI(responseBody.getMatches());
        }
        @Override
        public void onException(String errorMessage) {
            hideLoading();
            showError(errorMessage);
        }
    });

    private void bindMatchesToUI(List<MatchEntity> matchEntities){
        if(matchEntities!=null){
            mMatchesAdapter.swapMatches(matchEntities);
            hideLoading();
            if(matchEntities.size() == 0){
                showError(R.string.no_recent_matches);
            }
        }else{
            showError(R.string.no_recent_matches);
        }
    }

    @Override
    public void onItemClick(MatchEntity match) {

    }
}
