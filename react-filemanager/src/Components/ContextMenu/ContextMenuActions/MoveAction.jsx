import React from 'react';
import MenuItem from '@material-ui/core/MenuItem';
import { connect } from 'react-redux';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import Typography from '@material-ui/core/Typography';
import HowToVoteIcon from '@material-ui/icons/HowToVote';
import { initSubList, setVisibleDialogMove } from '../../../Actions/Actions.js';

function MoveAction(props) {
    const {handleClick, selectedFiles} = props;

    return (
        <MenuItem onClick={(e) => handleClick(e, selectedFiles)}>
            <ListItemIcon>
                <HowToVoteIcon />
            </ListItemIcon>
            <Typography variant="inherit">
                Move
            </Typography>
        </MenuItem>        
    );
}

const mapStateToProps = (state) => {
    return {
        selectedFiles: state.selectedFiles
    };
};

const mapDispatchToProps = (dispatch, ownProps) => {
    return {
        handleClick: (event, selectedFiles) => {
            dispatch(initSubList());
            dispatch(setVisibleDialogMove(true));
        }
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(MoveAction);
