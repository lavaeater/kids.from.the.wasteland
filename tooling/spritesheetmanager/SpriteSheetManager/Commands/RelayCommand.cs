using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace SpriteSheetManager.Commands
{
    public class RelayCommand : ICommand
    {
        private readonly Func<bool> _canExecuteFunction;
        private Action _executeMethod;

        public RelayCommand(Action executeMethod)
        {
            _executeMethod = executeMethod;
        }

        public RelayCommand(Action executeMethod, Func<bool> canExecuteFunction) : this(executeMethod)
        {
            _canExecuteFunction = canExecuteFunction;
        }

        public bool CanExecute(object parameter)
        {
            if (_canExecuteFunction != null)
            {
                return _canExecuteFunction();
            }

            if (_executeMethod != null)
            {
                return true;
            }

            return false;
        }

        public void Execute(object parameter)
        {
            _executeMethod?.Invoke();
        }

        public event EventHandler CanExecuteChanged;
    }    
}
